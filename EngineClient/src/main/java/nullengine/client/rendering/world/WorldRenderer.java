package nullengine.client.rendering.world;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.entity.EntityRenderManagerImpl;
import nullengine.client.rendering.game3d.Scene;
import nullengine.client.rendering.gl.GLStreamedRenderer;
import nullengine.client.rendering.gl.shader.ShaderProgram;
import nullengine.client.rendering.gl.shader.ShaderType;
import nullengine.client.rendering.gl.texture.GLFrameBuffer;
import nullengine.client.rendering.light.DirectionalLight;
import nullengine.client.rendering.material.Material;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.client.rendering.world.chunk.ChunkRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public class WorldRenderer {

    // TODO: adjustable width and height
    public static final int SHADOW_WIDTH = 2048;
    public static final int SHADOW_HEIGHT = 2048;

    private final ChunkRenderer chunkRenderer = new ChunkRenderer();
    private final BlockSelectionRenderer blockSelectionRenderer = new BlockSelectionRenderer();
    private final SkyboxRenderer skyboxRenderer = new SkyboxRenderer();

    private final EntityRenderManagerImpl entityRenderManager = new EntityRenderManagerImpl();

    private Scene scene;

    private ObservableValue<ShaderProgram> worldShader;
    private ObservableValue<ShaderProgram> entityShader;
    private GLFrameBuffer frameBuffer;
    private GLFrameBuffer frameBufferMultiSample;
    private ObservableValue<ShaderProgram> frameBufferSP;
    private GLFrameBuffer frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ObservableValue<ShaderProgram> shadowShader;

    private RenderManager context;

    public void init(RenderManager context, Scene scene) {
        this.context = context;
        this.scene = scene;

        chunkRenderer.init(context, scene);
        blockSelectionRenderer.init(context);
        entityRenderManager.init(context);
        skyboxRenderer.init(context);

        scene.getLightManager().getDirectionalLights().add(new DirectionalLight()
                .setDirection(new Vector3f(-0.15f, -1f, -0.35f))
                .setAmbient(new Vector3f(0.4f))
                .setDiffuse(new Vector3f(1f))
                .setSpecular(new Vector3f(1f)));
        scene.setMaterial(new Material().setAmbientColor(new Vector3f(0.5f))
                .setDiffuseColor(new Vector3f(1.0f))
                .setSpecularColor(new Vector3f(1.0f)).setShininess(32f));

        worldShader = ShaderManager.instance().registerShader("world_shader", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/world.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/world.frag")));
        entityShader = ShaderManager.instance().registerShader("entity_shader", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/entity.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/entity.frag")));

        frameBuffer = GLFrameBuffer.createRGB16FDepth24Stencil8FrameBuffer(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBufferMultiSample = GLFrameBuffer.createMultiSampleRGB16FDepth24Stencil8FrameBuffer(context.getWindow().getWidth(), context.getWindow().getHeight(), 1);
        frameBufferSP = ShaderManager.instance().registerShader("frame_buffer_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/framebuffer.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/framebuffer.frag")));
        //TODO init Client shader in a formal way
        frameBufferShadow = GLFrameBuffer.createDepthFrameBuffer(SHADOW_WIDTH, SHADOW_HEIGHT);
        shadowShader = ShaderManager.instance().registerShader("shadow_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/shadow.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/shadow.frag")));
    }

    public void render(float partial) {
        if (context.getWindow().isResized()) {
            frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
            frameBufferMultiSample.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        }

        // shadow
        glDisable(GL_MULTISAMPLE);
        frameBufferShadow.bind();
        GL11.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        ShaderProgram shadowShader = this.shadowShader.getValue();
        ShaderManager.instance().bindShaderOverriding(shadowShader);
        var lightProj = new Matrix4f().ortho(-10f * 2, 10f * 2, -10f * 2, 10f * 2, 1.0f / 2, 7.5f * 2);

        var lightView = new Matrix4f().lookAt(new Vector3f(-0.15f, -1f, -0.35f).negate().mul(8).add(0, 5, 0), new Vector3f(0, 5, 0), new Vector3f(0, 1, 0));

        var lightSpaceMat = new Matrix4f();
        lightProj.mul(lightView, lightSpaceMat);

        ShaderManager.instance().setUniform("u_LightSpace", lightSpaceMat);
        ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f().setTranslation(0, 0, 0));
        glCullFace(GL_FRONT);
        chunkRenderer.render();
        glCullFace(GL_BACK);

        ShaderManager.instance().unbindOverriding();

        // render chunk
        GL11.glViewport(0, 0, context.getWindow().getWidth(), context.getWindow().getHeight());

        frameBufferMultiSample.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        // TODO: Move it
        ShaderManager.instance().bindShader("chunk_solid");
        ShaderProgram chunkSolidShader = ShaderManager.instance().getShader("chunk_solid").getValue();
        if (chunkSolidShader != null) {
            ShaderManager.instance().setUniform("u_LightSpace", lightSpaceMat);
            ShaderManager.instance().setUniform("u_ShadowMap", 8);
        }
        GL15.glActiveTexture(GL13.GL_TEXTURE8);
        frameBufferShadow.getTexture(GL_DEPTH_ATTACHMENT).bind();
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        chunkRenderer.render();

        // render world
        ShaderManager.instance().bindShader(worldShader.getValue());
        ShaderManager.instance().setUniform("u_ProjMatrix", context.getProjectionMatrix());
        ShaderManager.instance().setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f());
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        skyboxRenderer.render(partial);
        blockSelectionRenderer.render(partial);

        renderEntity(lightSpaceMat, partial);

        // multi sample
        frameBuffer.bind();
        frameBuffer.blitFrom(frameBufferMultiSample);
        GLFrameBuffer.bindDefaultFrameBuffer();
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderManager.instance().bindShader(frameBufferSP.getValue());
        glDisable(GL_DEPTH_TEST);
        drawFrameBufferToScreen(frameBuffer);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_MULTISAMPLE);
    }

    private void drawFrameBufferToScreen(GLFrameBuffer frameBuffer) {
        frameBuffer.getTexture(GL_COLOR_ATTACHMENT0).bind();
        GLStreamedRenderer t = GLStreamedRenderer.getInstance();
        VertexDataBuf bb = t.getBuffer();
        bb.begin(VertexFormat.POSITION_TEX_COORD);
        bb.pos(-1.0f, 1.0f, 0).tex(0, 1.0f).endVertex();
        bb.pos(-1.0f, -1.0f, 0).tex(0, 0).endVertex();
        bb.pos(1.0f, 1.0f, 0).tex(1.0f, 1.0f).endVertex();
        bb.pos(1.0f, -1.0f, 0).tex(1.0f, 0).endVertex();
        t.draw(DrawMode.TRIANGLES_STRIP);
    }

    private void renderEntity(Matrix4f lightSpaceMat, float partial) {
        ShaderProgram shader = entityShader.getValue();
        ShaderManager.instance().bindShader(shader);
        shader.setUniform("u_ProjMatrix", context.getProjectionMatrix());
        shader.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        shader.setUniform("u_LightSpace", lightSpaceMat);
        shader.setUniform("u_ShadowMap", 8);
        scene.getLightManager().bind(context.getCamera(), shader);
        scene.getMaterial().bind(shader, "material");
        context.getEngine().getCurrentGame().getClientWorld().getEntities().forEach(entity ->
                entityRenderManager.render(entity, partial));
    }

    public void dispose() {
        chunkRenderer.dispose();
        blockSelectionRenderer.dispose();
        entityRenderManager.dispose();

        frameBuffer.dispose();
        frameBufferMultiSample.dispose();
        frameBufferShadow.dispose();

        ShaderManager.instance().unregisterShader("world_shader");
        ShaderManager.instance().unregisterShader("entity_shader");
        ShaderManager.instance().unregisterShader("frame_buffer_shader");
        ShaderManager.instance().unregisterShader("shadow_shader");
    }
}