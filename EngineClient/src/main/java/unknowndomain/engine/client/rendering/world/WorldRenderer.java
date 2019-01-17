package unknowndomain.engine.client.rendering.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.model.assimp.AssimpHelper;
import unknowndomain.engine.client.rendering.model.assimp.AssimpModel;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.util.*;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;
import unknowndomain.engine.util.Color;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.shader.Shader.setUniform;

public class WorldRenderer implements Renderer {

    private final ShaderProgram worldShader;

    private final ChunkRenderer chunkRenderer;

    private FrameBuffer frameBuffer;
    private FrameBuffer frameBufferMultisampled;
    private final DefaultFBOWrapper defaultFBO = new DefaultFBOWrapper();
    private ShaderProgram frameBufferSP;
    private FrameBufferShadow frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ShaderProgram shadowShader;

    private ClientContext context;

    private int u_Projection;
    private int u_View;
    private int u_Model;
    private int u_UsingColor;
    private int u_UsingTexture;

    public WorldRenderer(Shader vertex, Shader frag, ChunkRenderer chunkRenderer) {
        this.chunkRenderer = chunkRenderer;
        worldShader = ShaderManager.INSTANCE.createShader("world_shader", vertex,frag);
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
        frameBuffer = new FrameBuffer();
        frameBuffer.createFrameBuffer();
        frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBufferMultisampled = new FrameBufferMultiSampled();
        frameBufferMultisampled.createFrameBuffer();
        frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBuffer.check();
        frameBufferMultisampled.check();
        frameBufferSP = ShaderManager.INSTANCE.createShader("frame_buffer_shader",
                Shader.create(GLHelper.readText("/assets/unknowndomain/shader/framebuffer.vert"), ShaderType.VERTEX_SHADER),
                Shader.create(GLHelper.readText("/assets/unknowndomain/shader/framebuffer.frag"), ShaderType.FRAGMENT_SHADER)
        ); //TODO init shader in a formal way
        frameBufferShadow = new FrameBufferShadow();
        frameBufferShadow.createFrameBuffer();
        shadowShader = ShaderManager.INSTANCE.createShader("shadow_shader", Shader.create(GLHelper.readText("/assets/unknowndomain/shader/shadow.vert"), ShaderType.VERTEX_SHADER),
                Shader.create(GLHelper.readText("/assets/unknowndomain/shader/shadow.frag"), ShaderType.FRAGMENT_SHADER));

    }

    @Override
    public void render() {

        frameBufferShadow.bind();
        GL11.glViewport(0,0, FrameBufferShadow.SHADOW_WIDTH,FrameBufferShadow.SHADOW_HEIGHT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        ShaderManager.INSTANCE.bindShaderOverriding(shadowShader);
        var lightProj = new Matrix4f().ortho(-10f*2,10f*2,-10f*2, 10f*2, 1.0f/2,7.5f*2);

        var lightView = new Matrix4f().lookAt(new Vector3f(-0.15f,-1f,-0.35f).negate().mul(8).add(0,5,0), new Vector3f(0,5,0), new Vector3f(0,1,0));

        var lightSpaceMat = new Matrix4f();
        lightProj.mul(lightView, lightSpaceMat);

        shadowShader.setUniform("u_lightSpace", lightSpaceMat);
        shadowShader.setUniform("u_ModelMatrix", new Matrix4f().setTranslation(0,0,0));
        GL11.glCullFace(GL_FRONT);
        chunkRenderer.render();
        GL11.glCullFace(GL_BACK);

        ShaderManager.INSTANCE.unbindOverriding();
        frameBufferShadow.unbind();

        GL11.glViewport(0,0, context.getWindow().getWidth(),context.getWindow().getHeight());

        frameBufferMultisampled.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        ShaderManager.INSTANCE.bindShader("chunk_solid");
        ShaderManager.INSTANCE.getShader("chunk_solid").ifPresent(program -> program.setUniform("u_LightSpace", lightSpaceMat));
        ShaderManager.INSTANCE.getShader("chunk_solid").ifPresent(program -> program.setUniform("u_ShadowMap", 8));
        GL15.glActiveTexture(GL13.GL_TEXTURE8);
        GL11.glBindTexture(GL_TEXTURE_2D, frameBufferShadow.getDstexid());
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        chunkRenderer.render();

        ShaderManager.INSTANCE.bindShader(worldShader);
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

        RayTraceBlockHit hit = context.getHit();
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
        frameBuffer.bind();
        glEnable(GL_DEPTH_TEST);
        frameBuffer.blitFrom(frameBufferMultisampled);
        defaultFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderManager.INSTANCE.bindShader(frameBufferSP);
        glDisable(GL_DEPTH_TEST);
        defaultFBO.drawFrameBuffer(frameBuffer);

        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);

        if(context.getWindow().isResized()){
            frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
            frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        }
    }

    @Override
    public void dispose() {
        worldShader.dispose();
    }
}