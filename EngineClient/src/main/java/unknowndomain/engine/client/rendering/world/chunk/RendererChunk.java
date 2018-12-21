package unknowndomain.engine.client.rendering.world.chunk;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;
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

public class RendererChunk implements Renderer {

    private final ShaderProgram chunkSolidShader;

    private final Map<ChunkPos, ChunkMesh> loadedChunkMeshes = new HashMap<>();
    private final RenderChunkTask renderChunkTask = new RenderChunkTask();
    private final int u_ProjMatrix;
    private final int u_ViewMatrix;

    public RendererChunk(Shader vertex, Shader frag) {
        chunkSolidShader = new ShaderProgram();
        chunkSolidShader.init(vertex, frag);
        u_ProjMatrix = chunkSolidShader.getUniformLocation("u_ProjMatrix");
        u_ViewMatrix = chunkSolidShader.getUniformLocation("u_ViewMatrix");
    }

    @Override
    public void render(RenderContext context) {
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

    @Override
    public void dispose() {
        chunkSolidShader.dispose();
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
