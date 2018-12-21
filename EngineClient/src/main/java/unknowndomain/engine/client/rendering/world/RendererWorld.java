package unknowndomain.engine.client.rendering.world;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
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

import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public class RendererWorld extends ShaderProgram {

    protected final int A_POSITION = 0;
    protected final int A_TEXTCOORD = 1;
    protected final int A_NORMAL = 2;
    protected final int A_COLOR = 3;

    private int u_Projection;
    private int u_View;
    private int u_Model;

    private final Map<ChunkPos, ChunkMesh> loadedChunkMeshes = new HashMap<>();
    private final RenderChunkTask renderChunkTask = new RenderChunkTask();

    public RendererWorld(Shader vertex, Shader frag) {
        createShader(vertex, frag);

        useProgram();
        u_Projection = getUniformLocation("u_ProjMatrix");
        u_View = getUniformLocation("u_ViewMatrix");
        u_Model = getUniformLocation("u_ModelMatrix");
    }

    @Override
    public void render(RenderContext context) {
        Camera camera = context.getCamera();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        useProgram();

        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Shader.setUniform(u_Projection, context.getWindow().projection());

        Shader.setUniform(u_View, camera.view((float) context.partialTick()));

        Shader.setUniform(u_Model, new Matrix4f().setTranslation(0, 0, 0));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        setUniform("u_UseColor", true);
        setUniform("u_UseTexture", false);
        buffer.begin(GL11.GL_LINES, true, true, false);
        buffer.pos(0, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(100, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 100, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 1).endVertex();
        buffer.pos(0, 0, 100).color(0, 0, 1).endVertex();
        tessellator.draw();

        setUniform("u_UseColor", false);
        setUniform("u_UseTexture", true);
        context.getTextureManager().getTextureAtlas(BLOCK).bind();
        for (ChunkMesh chunkMesh : loadedChunkMeshes.values()) {
            if(!chunkMesh.getChunk().isEmpty()) {
                if(chunkMesh.isDirty()) {
                    renderChunkTask.updateChunkMesh(chunkMesh);
                }
                chunkMesh.render();
            }
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
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
}