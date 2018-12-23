package unknowndomain.engine.client.rendering.world.chunk;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.block.BlockRenderer;
import unknowndomain.engine.client.rendering.block.ModelBlockRenderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.world.block.BlockChangeEvent;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.ChunkPos;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;
import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public class ChunkRenderer implements Renderer {

    private final BlockRenderer blockRenderer = new ModelBlockRenderer();
    private final ShaderProgram chunkSolidShader;

    private final Map<ChunkPos, ChunkMesh> loadedChunkMeshes = new HashMap<>();

    private final ThreadPoolExecutor updateExecutor;
    private final BlockingQueue<Runnable> uploadTasks = new LinkedBlockingQueue<>();

    private final int u_ProjMatrix, u_ViewMatrix;

    private RenderContext context;

    public ChunkRenderer(Shader vertex, Shader frag) {
        chunkSolidShader = new ShaderProgram();
        chunkSolidShader.init(vertex, frag);
        u_ProjMatrix = chunkSolidShader.getUniformLocation("u_ProjMatrix");
        u_ViewMatrix = chunkSolidShader.getUniformLocation("u_ViewMatrix");

        ThreadGroup threadGroup = new ThreadGroup("Chunk Baker");
        int threadCount = Runtime.getRuntime().availableProcessors() / 2;
        this.updateExecutor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new BakeChunkThread(threadGroup, r, "Chunk Baker " + poolNumber.getAndIncrement());
            }
        });
    }

    @Override
    public void init(RenderContext context) {
        this.context = context;
    }

    @Override
    public void render() {
        preRenderChunk();

        handleUploadTask();

        for (ChunkMesh chunkMesh : loadedChunkMeshes.values()) {
            chunkMesh.render();
        }

        postRenderChunk();
    }

    protected void preRenderChunk() {
        chunkSolidShader.use();

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        setUniform(u_ProjMatrix, context.getWindow().projection());
        setUniform(u_ViewMatrix, context.getCamera().view((float) context.partialTick()));

        context.getTextureManager().getTextureAtlas(BLOCK).bind();
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

    public BlockRenderer getBlockRenderer() {
        return blockRenderer;
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
        markDirty(loadedChunkMeshes.computeIfAbsent(event.getPos(), pos -> new ChunkMesh(pos, event.getChunk())));
    }

    @Listener
    public void onBlockChange(BlockChangeEvent event) {
        ChunkMesh chunkMesh = loadedChunkMeshes.get(event.getPos().toChunkPos());
        if (chunkMesh == null)
            return;

        markDirty(chunkMesh);
    }

    private void markDirty(ChunkMesh chunkMesh) {
        if (!chunkMesh.isDirty()) {
            chunkMesh.markDirty();
            addBakeChunkTask(chunkMesh);
        } else {
            chunkMesh.markDirty();
        }
    }

    private void addBakeChunkTask(ChunkMesh chunkMesh) {
        updateExecutor.execute(new BakeChunkTask(this, chunkMesh, getDistanceSqChunkToCamera(chunkMesh.getChunkPos())));
    }

    private double getDistanceSqChunkToCamera(ChunkPos chunkPos) {
        // FIXME:
        if (context.getCamera() == null) {
            return 0;
        }

        Vector3f position = context.getCamera().getPosition(0);
        double x = (chunkPos.getX() << 4) + 8 - position.x;
        double y = (chunkPos.getY() << 4) + 8 - position.y;
        double z = (chunkPos.getZ() << 4) + 8 - position.z;
        return x * x + y * y + z * z;
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
