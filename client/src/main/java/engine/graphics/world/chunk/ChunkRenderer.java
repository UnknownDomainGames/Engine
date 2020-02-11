package engine.graphics.world.chunk;

import com.github.mouse0w0.observable.value.ObservableValue;
import engine.client.asset.AssetURL;
import engine.client.game.GameClient;
import engine.event.Listener;
import engine.event.block.BlockChangeEvent;
import engine.event.world.chunk.ChunkLoadEvent;
import engine.graphics.RenderManager;
import engine.graphics.Scene3D;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.gl.shader.ShaderType;
import engine.graphics.material.Material;
import engine.graphics.shader.ShaderManager;
import engine.graphics.shader.ShaderProgramBuilder;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexDataBufPool;
import engine.graphics.viewport.Viewport;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.math.BlockPos;
import engine.world.World;
import engine.world.chunk.Chunk;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static engine.world.chunk.ChunkConstants.*;
import static org.lwjgl.opengl.GL11.*;

public final class ChunkRenderer {

    private final LongObjectMap<ChunkMesh> loadedChunkMeshes = new LongObjectHashMap<>();
    private final BlockingQueue<Runnable> uploadTasks = new LinkedBlockingQueue<>();
    private final VertexDataBufPool bufferPool = VertexDataBufPool.create(0x200000, 64);

    private final ObservableValue<ShaderProgram> chunkSolidShader;

    private final RenderManager manager;
    private final GameClient game;
    private final Viewport viewport;
    private final Scene3D scene;
    private final World world;

    private final ThreadPoolExecutor chunkBakeExecutor;

    @Deprecated
    private final Material material;

    public ChunkRenderer(RenderManager manager, World world) {
        this.manager = manager;
        this.game = manager.getEngine().getCurrentGame();
        game.getEventBus().register(this);
        this.viewport = manager.getViewport();
        this.scene = viewport.getScene();
        this.world = world;

        chunkSolidShader = ShaderManager.instance().registerShader("chunk_solid", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/chunk_solid.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/chunk_solid.frag")));

        material = new Material().setAmbientColor(new Vector3f(0.5f))
                .setDiffuseColor(new Vector3f(1.0f))
                .setSpecularColor(new Vector3f(1.0f)).setShininess(32f);

        // TODO: Configurable and manage
        int threadCount = Runtime.getRuntime().availableProcessors() / 2;
        this.chunkBakeExecutor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Chunk Baker " + poolNumber.getAndIncrement());
            }
        });

        VoxelRenderHelper.getVoxelTextureAtlas().reload();
        initWorld(world);
    }

    public void render() {
        preRender();

        runUploadTasks();

        synchronized (loadedChunkMeshes) {
            for (var entry : loadedChunkMeshes.entrySet()) {
                var mesh = entry.getValue();
                if (shouldRenderChunk(mesh)) {
                    mesh.render();
                }
            }
        }

        postRender();
    }

    private boolean shouldRenderChunk(ChunkMesh mesh) {
        Vector3ic min = mesh.getChunk().getMin();
        Vector3ic max = mesh.getChunk().getMax();
        return viewport.getFrustum().testAab(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    private void preRender() {
        ShaderProgram shader = this.chunkSolidShader.getValue();
        ShaderManager.instance().bindShader(shader);

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_DEPTH_TEST);

        shader.setUniform("u_ProjMatrix", viewport.getProjectionMatrix());
        shader.setUniform("u_ViewMatrix", viewport.getCamera().getViewMatrix());
        shader.setUniform("u_ModelMatrix", new Matrix4f());
        shader.setUniform("u_viewPos", viewport.getCamera().getPosition());

        VoxelRenderHelper.getVoxelTextureAtlas().bind();
        shader.setUniform("useDirectUV", true);
        scene.getLightManager().bind(viewport.getCamera().getPosition(), shader);
        material.bind(shader, "material");
    }

    private void postRender() {
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    public void dispose() {
        clearChunkMeshes();
        chunkBakeExecutor.shutdownNow();

        ShaderManager.instance().unregisterShader("chunk_solid");

        game.getEventBus().unregister(this);
    }

    public VertexDataBufPool getBufferPool() {
        return bufferPool;
    }

    public void uploadChunk(ChunkMesh chunkMesh, VertexDataBuf buffer) {
        uploadTasks.add(() -> {
            chunkMesh.upload(buffer);
            bufferPool.free(buffer);
        });
    }

    private void runUploadTasks() {
        Runnable runnable;
        while ((runnable = uploadTasks.poll()) != null) {
            runnable.run();
        }
    }

    @Listener
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (world.equals(chunk.getWorld())) {
            initChunk(chunk);
        }
    }

    @Listener
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos().toUnmodifiable();
        int chunkX = pos.x() >> CHUNK_X_BITS,
                chunkY = pos.y() >> CHUNK_Y_BITS,
                chunkZ = pos.z() >> CHUNK_Z_BITS;
        markChunkDirty(getChunkIndex(event.getPos()));

        // Mark neighbor chunk dirty.
        int chunkW = pos.x() + 1 >> CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.x() - 1 >> CHUNK_X_BITS;
        if (chunkW != chunkX) {
            markChunkDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.y() + 1 >> CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.y() - 1 >> CHUNK_Y_BITS;
        if (chunkW != chunkY) {
            markChunkDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.z() + 1 >> CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
        chunkW = pos.z() - 1 >> CHUNK_Z_BITS;
        if (chunkW != chunkZ) {
            markChunkDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
    }

    private void initChunk(Chunk chunk) {
        long chunkIndex = getChunkIndex(chunk);
        synchronized (loadedChunkMeshes) {
            loadedChunkMeshes.computeIfAbsent(chunkIndex, key -> new ChunkMesh(chunk));
        }
        markChunkDirty(chunkIndex);
    }

    private void initWorld(World world) {
        world.getLoadedChunks().forEach(this::initChunk);
    }

    private void clearChunkMeshes() {
        loadedChunkMeshes.values().forEach(ChunkMesh::dispose);
        loadedChunkMeshes.clear();
        uploadTasks.clear();
    }

    private void markChunkDirty(long index) {
        ChunkMesh chunkMesh = loadedChunkMeshes.get(index);
        if (chunkMesh == null) {
            return;
        }

        if (!chunkMesh.isDirty()) {
            chunkMesh.markDirty();
            addBakeChunkTask(chunkMesh);
        }
    }

    private void addBakeChunkTask(ChunkMesh chunkMesh) {
        chunkBakeExecutor.execute(new BakeChunkTask(this, chunkMesh, getDistanceSqChunkToCamera(chunkMesh.getChunk())));
    }

    private double getDistanceSqChunkToCamera(Chunk chunk) {
        Vector3fc position = viewport.getCamera().getPosition();
        Vector3ic center = chunk.getCenter();
        return position.distanceSquared(center.x(), center.y(), center.z());
    }
}
