package unknowndomain.engine.client.rendering.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.light.DirectionalLight;
import unknowndomain.engine.client.rendering.light.Light;
import unknowndomain.engine.client.rendering.light.Material;
import unknowndomain.engine.client.rendering.light.PointLight;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.util.GLHelper;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.world.block.BlockChangeEvent;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.chunk.Chunk;
import unknowndomain.engine.world.chunk.ChunkStorage;

import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;
import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;
import static unknowndomain.engine.world.chunk.ChunkConstants.*;

public class ChunkRenderer implements Renderer {

    private final ShaderProgram chunkSolidShader;

    private final LongObjectMap<ChunkMesh> loadedChunkMeshes = new LongObjectHashMap<>();

    private final ThreadPoolExecutor updateExecutor;
    private final BlockingQueue<Runnable> uploadTasks = new LinkedBlockingQueue<>();

    private ClientContext context;

    public ChunkRenderer(Shader vertex, Shader frag) {
        chunkSolidShader = ShaderManager.INSTANCE.createShader("chunk_solid", vertex,frag);

        // TODO: Configurable
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
    }

    Light dirLight, ptLight;
    Material mat;

    @Override
    public void init(ClientContext context) {
        this.context = context;

        //tmp = AssimpHelper.loadModel("assets/tmp/untitled.obj");
        dirLight = new DirectionalLight().setDirection(new Vector3f(-0.15f,-1f,-0.35f))
                .setAmbient(new Vector3f(0.4f))
                .setDiffuse(new Vector3f(1.0f,1f,1f))
                .setSpecular(new Vector3f(1.0f));
        ptLight = new PointLight().setPosition(new Vector3f(3.5f,7.5f,-3.5f)).setKlinear(0.7f).setKquadratic(1.8f).setAmbient(new Vector3f(0.1f)).setDiffuse(new Vector3f(0.0f*.5f,0.0f*.5f,0.9f)).setSpecular(new Vector3f(0.6f));
        mat = new Material().setAmbientColor(new Vector3f(0.5f))
                .setDiffuseColor(new Vector3f(1.0f))
                .setSpecularColor(new Vector3f(1.0f)).setShininess(32f);
    }

    @Override
    public void render() {
        preRenderChunk();

        handleUploadTask();

        for (ChunkMesh chunkMesh : loadedChunkMeshes.values()) {
            if (context.getFrustumIntersection().testAab(chunkMesh.getChunk().getMin(), chunkMesh.getChunk().getMax())) {
                chunkMesh.render();
            }
        }
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f().setTranslation(0,5,0));
//        tmp.render();

        postRenderChunk();
    }

    protected void preRenderChunk() {
        ShaderManager.INSTANCE.bindShader(chunkSolidShader);

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        Matrix4f projMatrix = context.getWindow().projection();
        Matrix4f viewMatrix = context.getCamera().view((float) context.partialTick());
        Matrix4f modelMatrix = new Matrix4f().setTranslation(0,0,0);
        ShaderManager.INSTANCE.setUniform("u_ProjMatrix", projMatrix);
        ShaderManager.INSTANCE.setUniform("u_ViewMatrix", viewMatrix);
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", modelMatrix);
        chunkSolidShader.setUniform("u_viewPos", context.getCamera().getPosition(0));

        context.getTextureManager().getTextureAtlas(BLOCK).bind();
        chunkSolidShader.setUniform("useDirectUV", true);
        dirLight.bind(chunkSolidShader, "dirLights[0]");
        //ptLight.bind(chunkSolidShader,"pointLights[0]");
        mat.bind(chunkSolidShader, "material");
    }

    protected void postRenderChunk() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    @Override
    public void dispose() {
        updateExecutor.shutdown();
        chunkSolidShader.dispose();
    }

    public ClientContext getContext() {
        return context;
    }

    public void upload(ChunkMesh chunkMesh, BufferBuilder buffer) {
        ByteBuffer finalBuffer = BufferUtils.createByteBuffer(buffer.build().limit());
        finalBuffer.put(buffer.build());
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
        long chunkIndex = getChunkIndex(event.getChunk());
        loadedChunkMeshes.put(chunkIndex, new ChunkMesh(event.getChunk()));
        markDirty(chunkIndex);
    }

    @Listener
    public void onBlockChange(BlockChangeEvent.Post event) {
        BlockPos pos = event.getPos().toImmutable();
        int chunkX = pos.getX() >> BITS_X,
                chunkY = pos.getY() >> BITS_Y,
                chunkZ = pos.getZ() >> BITS_Z;
        markDirty(getChunkIndex(event.getPos()));

        // Update neighbor chunks.
        int chunkW = pos.getX() + 1 >> BITS_X;
        if (chunkW != chunkX) {
            markDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.getX() - 1 >> BITS_X;
        if (chunkW != chunkX) {
            markDirty(getChunkIndex(chunkW, chunkY, chunkZ));
        }
        chunkW = pos.getY() + 1 >> BITS_Y;
        if (chunkW != chunkY) {
            markDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.getY() - 1 >> BITS_Y;
        if (chunkW != chunkY) {
            markDirty(getChunkIndex(chunkX, chunkW, chunkZ));
        }
        chunkW = pos.getZ() + 1 >> BITS_Z;
        if (chunkW != chunkZ) {
            markDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
        chunkW = pos.getZ() - 1 >> BITS_Z;
        if (chunkW != chunkZ) {
            markDirty(getChunkIndex(chunkX, chunkY, chunkW));
        }
    }

    private void markDirty(long index) {
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

        Vector3f position = context.getCamera().getPosition(0);
        double x = chunk.getMin().x() + 8 - position.x;
        double y = chunk.getMin().y() + 8 - position.y;
        double z = chunk.getMin().z() + 8 - position.z;
        return x * x + y * y + z * z;
    }

    // TODO: Merge with ChunkStorage

    public static long getChunkIndex(BlockPos pos) {
        return getChunkIndex(pos.getX() >> BITS_X, pos.getY() >> BITS_Y, pos.getZ() >> BITS_Z);
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
