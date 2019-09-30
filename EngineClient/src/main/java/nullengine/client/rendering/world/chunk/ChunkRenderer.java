package nullengine.client.rendering.world.chunk;

import com.github.mouse0w0.observable.value.ObservableValue;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.client.asset.AssetURL;
import nullengine.client.game.GameClient;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.shader.ShaderType;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferPool;
import nullengine.client.rendering.world.WorldRenderer;
import nullengine.event.Listener;
import nullengine.event.block.BlockChangeEvent;
import nullengine.event.player.PlayerControlEntityEvent;
import nullengine.event.world.chunk.ChunkLoadEvent;
import nullengine.math.BlockPos;
import nullengine.world.World;
import nullengine.world.chunk.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static nullengine.world.chunk.ChunkConstants.*;
import static org.lwjgl.opengl.GL11.*;

public class ChunkRenderer {

    private final LongObjectMap<ChunkMesh> loadedChunkMeshes = new LongObjectHashMap<>();
    private final BlockingQueue<Runnable> uploadTasks = new LinkedBlockingQueue<>();

    private final GLBufferPool bufferPool = GLBufferPool.createDirectBufferPool(0x200000, 64);

    private World world;

    private ObservableValue<ShaderProgram> chunkSolidShader;
    private ObservableValue<ShaderProgram> assimpShader;

    private RenderManager context;
    private GameClient game;
    private WorldRenderer worldRenderer;

    private ThreadPoolExecutor chunkBakeExecutor;


    public void init(RenderManager context, WorldRenderer worldRenderer) {
        this.context = context;
        this.game = context.getEngine().getCurrentGame();
        game.getEventBus().register(this);
        this.worldRenderer = worldRenderer;

        chunkSolidShader = ShaderManager.instance().registerShader("chunk_solid", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/chunk_solid.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/chunk_solid.frag")));
        assimpShader = ShaderManager.instance().registerShader("assimp_model", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/assimp_model.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/chunk_solid.frag")));

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

        context.getTextureManager().getTextureAtlas(StandardTextureAtlas.DEFAULT).reload();
        initWorld(context.getEngine().getCurrentGame().getPlayer().getWorld());
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
        return context.getFrustumIntersection().testAab(mesh.getChunk().getMin(), mesh.getChunk().getMax());
    }

    private void preRender() {
        ShaderProgram chunkSolidShader = this.chunkSolidShader.getValue();
        ShaderManager.instance().bindShader(chunkSolidShader);

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        ShaderManager.instance().setUniform("u_ProjMatrix", context.getWindow().projection());
        ShaderManager.instance().setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f());
        ShaderManager.instance().setUniform("u_viewPos", context.getCamera().getPosition());

        context.getTextureManager().getTextureAtlas(StandardTextureAtlas.DEFAULT).bind();
        chunkSolidShader.setUniform("useDirectUV", true);
        worldRenderer.getLightManager().bind(context.getCamera());
        worldRenderer.getMaterial().bind("material");
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    public void dispose() {
        clearChunkMeshes();
        chunkBakeExecutor.shutdownNow();

        ShaderManager.instance().unregisterShader("chunk_solid");
        ShaderManager.instance().unregisterShader("assimp_model");

        game.getEventBus().unregister(this);
    }

    public GLBufferPool getBufferPool() {
        return bufferPool;
    }

    public void uploadChunk(ChunkMesh chunkMesh, GLBuffer buffer) {
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
        if (event.getChunk().getWorld() != world) {
            return;
        }

        initChunk(event.getChunk());
    }

    @Listener
    public void onPlayerControlEntity(PlayerControlEntityEvent.Post event) {
        initWorld(event.getNewEntity().getWorld());
    }

    @Listener
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos().toImmutable();
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
        if (this.world == world) {
            return;
        }

        clearChunkMeshes();

        this.world = world;
        if (world == null) {
            return;
        }

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
        // FIXME:
        if (context.getCamera() == null) {
            return 0;
        }

        Vector3fc position = context.getCamera().getPosition();
        double x = chunk.getMin().x() + 8 - position.x();
        double y = chunk.getMin().y() + 8 - position.y();
        double z = chunk.getMin().z() + 8 - position.z();
        return x * x + y * y + z * z;
    }
}
