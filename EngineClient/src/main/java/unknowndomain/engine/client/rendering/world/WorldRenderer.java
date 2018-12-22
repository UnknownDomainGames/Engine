package unknowndomain.engine.client.rendering.world;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;

public class WorldRenderer implements Renderer {

    private final ShaderProgram worldShader;

    private final ChunkRenderer chunkRenderer;

    private int u_Projection;
    private int u_View;
    private int u_Model;
    private int u_UsingColor;
    private int u_UsingTexture;

    public WorldRenderer(Shader vertex, Shader frag, ChunkRenderer chunkRenderer) {
        this.chunkRenderer = chunkRenderer;
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
        worldShader.use();

        glEnable(GL11.GL_DEPTH_TEST);
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        setUniform(u_Projection, context.getWindow().projection());

        setUniform(u_View, context.getCamera().view((float) context.partialTick()));

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

        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);

        chunkRenderer.render(context);
    }

    @Override
    public void dispose() {
        worldShader.dispose();
    }
}