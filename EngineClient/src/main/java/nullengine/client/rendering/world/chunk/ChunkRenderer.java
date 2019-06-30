package nullengine.client.rendering.world.chunk;

import com.github.mouse0w0.observable.value.ObservableValue;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import nullengine.client.asset.AssetPath;
import nullengine.client.game.GameClient;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.light.DirectionalLight;
import nullengine.client.rendering.light.Light;
import nullengine.client.rendering.light.Material;
import nullengine.client.rendering.light.PointLight;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.shader.ShaderType;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.event.Listener;
import nullengine.event.world.block.BlockChangeEvent;
import nullengine.event.world.chunk.ChunkLoadEvent;
import nullengine.math.BlockPos;
import nullengine.world.World;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.ChunkStorage;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;

public class ChunkRenderer {

    private final LongObjectMap<ChunkMesh> loadedChunkMeshes = new LongObjectHashMap<>();
    private final BlockingQueue<Runnable> uploadTasks = new LinkedBlockingQueue<>();

    private ObservableValue<ShaderProgram> chunkSolidShader;
    private ObservableValue<ShaderProgram> assimpShader;

    private RenderContext context;
    private GameClient game;

    private ThreadPoolExecutor updateExecutor;

    Light dirLight, ptLight;
    Material mat;

    public void init(RenderContext context) {
        this.context = context;
        this.game = context.getEngine().getCurrentGame();
        game.getEventBus().register(this);

        chunkSolidShader = ShaderManager.INSTANCE.registerShader("chunk_solid",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "chunk_solid.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "chunk_solid.frag")));
        assimpShader = ShaderManager.INSTANCE.registerShader("assimp_model",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "assimp_model.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "chunk_solid.frag")));

        //tmp = AssimpHelper.loadModel("assets/tmp/untitled.obj");
        dirLight = new DirectionalLight().setDirection(new Vector3f(-0.15f, -1f, -0.35f))
                .setAmbient(new Vector3f(0.4f))
                .setDiffuse(new Vector3f(1.0f, 1f, 1f))
                .setSpecular(new Vector3f(1.0f));
        ptLight = new PointLight().setPosition(new Vector3f(3.5f, 7.5f, -3.5f)).setKlinear(0.7f).setKquadratic(1.8f).setAmbient(new Vector3f(0.1f)).setDiffuse(new Vector3f(0.0f * .5f, 0.0f * .5f, 0.9f)).setSpecular(new Vector3f(0.6f));
        mat = new Material().setAmbientColor(new Vector3f(0.5f))
                .setDiffuseColor(new Vector3f(1.0f))
                .setSpecularColor(new Vector3f(1.0f)).setShininess(32f);

        // TODO: Configurable and manage
        int threadCount = Runtime.getRuntime().availableProcessors() / 2;
        this.updateExecutor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new BakeChunkThread(r, "Chunk Baker " + poolNumber.getAndIncrement());
            }
        });

        context.getTextureManager().reloadTextureAtlas(StandardTextureAtlas.BLOCK);
        initWorld(game.getWorld());
    }

    public void render() {
        preRenderChunk();

        handleUploadTask();

        for (ChunkMesh chunkMesh : loadedChunkMeshes.values()) {
            if (chunkMesh != null && context.getFrustumIntersection().testAab(chunkMesh.getChunk().getMin(), chunkMesh.getChunk().getMax())) {
                chunkMesh.render();
            }
        }
//        ShaderProgram assimpShader = this.assimpShader.getValue();
//        ShaderManager.INSTANCE.bindShader(assimpShader);
//        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f().rotate((float)-Math.PI / 2, 1,0,0).setTranslation(0,5,0));
//        tmp.render();

        postRenderChunk();
    }

    private void preRenderChunk() {
        ShaderProgram chunkSolidShader = this.chunkSolidShader.getValue();
        ShaderManager.INSTANCE.bindShader(chunkSolidShader);

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        Matrix4fc projMatrix = context.getWindow().projection();
        Matrix4f modelMatrix = new Matrix4f();
        ShaderManager.INSTANCE.setUniform("u_ProjMatrix", projMatrix);
        ShaderManager.INSTANCE.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", modelMatrix);
        ShaderManager.INSTANCE.setUniform("u_viewPos", context.getCamera().getPosition());

        context.getTextureManager().getTextureAtlas(StandardTextureAtlas.BLOCK).getTexture().getValue().bind();
        chunkSolidShader.setUniform("useDirectUV", true);
        dirLight.bind("dirLights[0]");
        //ptLight.bind(chunkSolidShader,"pointLights[0]");
        mat.bind("material");
    }

    private void postRenderChunk() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    public void dispose() {
        updateExecutor.shutdownNow();

        ShaderManager.INSTANCE.unregisterShader("chunk_solid");

        game.getEventBus().unregister(this);
    }

    public void upload(ChunkMesh chunkMesh, GLBuffer buffer) {
        ByteBuffer finalBuffer = BufferUtils.createByteBuffer(buffer.getBackingBuffer().limit());
        finalBuffer.put(buffer.getBackingBuffer());
        finalBuffer.flip();
        uploadTasks.add(new UploadTask(chunkMesh, finalBuffer, buffer.getVertexCount()));

        if (chunkMesh.isDirty()) {
            addBakeChunkTask(chunkMesh);
        }
    }

    public void handleUploadTask() {
        Runnable runnable;
        while ((runnable = uploadTasks.poll()) != null) {
            runnable.run();
        }
    }

    @Listener
    public void onChunkLoad(ChunkLoadEvent event) {
        // TODO: Fix it.
        initChunkMesh(event.getChunk());
    }

    private void initChunkMesh(Chunk chunk) {
        long chunkIndex = getChunkIndex(chunk);
        loadedChunkMeshes.put(chunkIndex, new ChunkMesh(chunk));
        markChunkMeshDirty(chunkIndex);
    }

    private void initWorld(World world) {
        loadedChunkMeshes.values().forEach(ChunkMesh::dispose);
        loadedChunkMeshes.clear();

        uploadTasks.clear();

        world.getLoadedChunks().forEach(this::initChunkMesh);
    }

    @Listener
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos().toImmutable();
        int chunkX = pos.getX() >> ChunkConstants.BITS_X,
                chunkY = pos.getY() >> ChunkConstants.BITS_Y,
                chunkZ = pos.getZ() >> ChunkConstants.BITS_Z;
        markChunkMeshDirty(getChunkIndex(event.getPos()));

        // Update neighbor chunks.
        int chunkW = pos.getX() + 1 >> ChunkConstants.BITS_X;
        if (chunkW != chunkX) {
            markChunkMeshDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.getX() - 1 >> ChunkConstants.BITS_X;
        if (chunkW != chunkX) {
            markChunkMeshDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.getY() + 1 >> ChunkConstants.BITS_Y;
        if (chunkW != chunkY) {
            markChunkMeshDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.getY() - 1 >> ChunkConstants.BITS_Y;
        if (chunkW != chunkY) {
            markChunkMeshDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.getZ() + 1 >> ChunkConstants.BITS_Z;
        if (chunkW != chunkZ) {
            markChunkMeshDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
        chunkW = pos.getZ() - 1 >> ChunkConstants.BITS_Z;
        if (chunkW != chunkZ) {
            markChunkMeshDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
    }

    private void markChunkMeshDirty(long index) {
        ChunkMesh chunkMesh = loadedChunkMeshes.get(index);
        if (chunkMesh == null) {
            return;
        }
        if (!chunkMesh.isDirty()) {
            chunkMesh.markDirty();
            addBakeChunkTask(chunkMesh);
        } else {
            chunkMesh.markDirty();
        }
    }

    private void addBakeChunkTask(ChunkMesh chunkMesh) {
        updateExecutor.execute(new BakeChunkTask(this, chunkMesh, getDistanceSqChunkToCamera(chunkMesh.getChunk())));
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

    // TODO: Merge with ChunkStorage

    public static long getChunkIndex(BlockPos pos) {
        return getChunkIndex(pos.getX() >> ChunkConstants.BITS_X, pos.getY() >> ChunkConstants.BITS_Y, pos.getZ() >> ChunkConstants.BITS_Z);
    }

    public static long getChunkIndex(Chunk chunk) {
        return getChunkIndex(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ());
    }

    public static long getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return (abs(chunkX) << 42) | (abs(chunkY) << 21) | abs(chunkZ);
    }

    private static long abs(long value) {
        return value >= 0 ? value : ChunkStorage.maxPositiveChunkPos - value;
    }

    private class UploadTask implements Runnable {

        private final ChunkMesh chunkMesh;
        private final ByteBuffer buffer;
        private final int vertexCount;

        public UploadTask(ChunkMesh chunkMesh, ByteBuffer buffer, int vertexCount) {
            this.chunkMesh = chunkMesh;
            this.buffer = buffer;
            this.vertexCount = vertexCount;
        }

        @Override
        public void run() {
            chunkMesh.upload(buffer, vertexCount);
        }
    }
}
