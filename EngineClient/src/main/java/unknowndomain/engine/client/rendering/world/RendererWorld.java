package unknowndomain.engine.client.rendering.world;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.world.chunk.ChunkMesh;
import unknowndomain.engine.client.rendering.world.chunk.RenderChunkTask;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.world.block.BlockChangeEvent;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;
import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public class RendererWorld implements Renderer {

    protected final int A_POSITION = 0;
    protected final int A_TEXTCOORD = 1;
    protected final int A_NORMAL = 2;
    protected final int A_COLOR = 3;

    private final ShaderProgram worldShader;

    private int u_Projection;
    private int u_View;
    private int u_Model;
    private int u_UsingColor;
    private int u_UsingTexture;

    private final Map<ChunkPos, ChunkMesh> loadedChunkMeshes = new HashMap<>();
    private final RenderChunkTask renderChunkTask = new RenderChunkTask();

    public RendererWorld(Shader vertex, Shader frag) {
        worldShader = new ShaderProgram();
        worldShader.init(vertex, frag);
        u_Projection = worldShader.getUniformLocation("u_ProjMatrix");
        u_View = worldShader.getUniformLocation("u_ViewMatrix");
        u_Model = worldShader.getUniformLocation("u_ModelMatrix");
        u_UsingColor = worldShader.getUniformLocation("u_UsingColor");
        u_UsingTexture = worldShader.getUniformLocation("u_UsingTexture");
    }

    @Override
    public void render(RenderContext context) {
        Camera camera = context.getCamera();

        glClear(GL11.GL_COLOR_BUFFER_BIT);
        worldShader.use();

        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        setUniform(u_Projection, context.getWindow().projection());

        setUniform(u_View, camera.view((float) context.partialTick()));

        setUniform(u_Model, new Matrix4f().setTranslation(0, 0, 0));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        setUniform(u_UsingColor, true);
        setUniform(u_UsingTexture, false);
        buffer.begin(GL11.GL_LINES, true, true, false);
        buffer.pos(0, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(100, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 100, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 1).endVertex();
        buffer.pos(0, 0, 100).color(0, 0, 1).endVertex();
        tessellator.draw();

        setUniform(u_UsingColor, true);
        setUniform(u_UsingTexture, true);
        GLTexture blockTextureAtlas = context.getTextureManager().getTextureAtlas(BLOCK);
        blockTextureAtlas.bind();
        for (ChunkMesh chunkMesh : loadedChunkMeshes.values()) {
            if (!chunkMesh.getChunk().isEmpty()) {
                if (chunkMesh.isDirty()) {
                    renderChunkTask.updateChunkMesh(chunkMesh);
                }
                chunkMesh.render();
            }
        }

        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        blockTextureAtlas.unbind();
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    @Listener
    public void handleChunkLoad(ChunkLoadEvent event) {
        loadedChunkMeshes.put(event.getPos(), new ChunkMesh(event.getWorld(), event.getPos(), event.getChunk()));
    }

    @Listener
    public void handleBlockChange(BlockChangeEvent event) {
        BlockPos pos = event.getPos();
        ChunkMesh chunkMesh = loadedChunkMeshes.get(event.getPos().toChunkPos());
        if (chunkMesh == null) {
            return;
        }

        chunkMesh.markDirty();
    }

    @Override
    public void dispose() {
        worldShader.dispose();
    }
}