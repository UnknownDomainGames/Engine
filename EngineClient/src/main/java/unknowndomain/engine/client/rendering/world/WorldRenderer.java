package unknowndomain.engine.client.rendering.world;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;
import unknowndomain.engine.util.Color;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;

public class WorldRenderer implements Renderer {

    private final ShaderProgram worldShader;

    private final ChunkRenderer chunkRenderer;

    private ClientContext context;

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
    public void init(ClientContext context) {
        this.context = context;
        chunkRenderer.init(context);
    }

    @Override
    public void render() {
        chunkRenderer.render();

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

        BlockPrototype.Hit hit = context.getHit();
        if (hit != null) {
            float minX = hit.getPos().getX() - 0.001f, maxX = hit.getPos().getX() + 1.001f,
                    minY = hit.getPos().getY() - 0.001f, maxY = hit.getPos().getY() + 1.001f,
                    minZ = hit.getPos().getZ() - 0.001f, maxZ = hit.getPos().getZ() + 1.001f;
            buffer.begin(GL11.GL_LINES, true, true, false);
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            tessellator.draw();
        }

        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    @Override
    public void dispose() {
        worldShader.dispose();
    }
}