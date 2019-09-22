package nullengine.client.rendering.world;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.entity.EntityRenderManagerImpl;
import nullengine.client.rendering.light.DirectionalLight;
import nullengine.client.rendering.light.LightManager;
import nullengine.client.rendering.material.Material;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.shader.ShaderType;
import nullengine.client.rendering.util.DefaultFBOWrapper;
import nullengine.client.rendering.util.FrameBuffer;
import nullengine.client.rendering.util.FrameBufferMultiSampled;
import nullengine.client.rendering.util.FrameBufferShadow;
import nullengine.client.rendering.world.chunk.ChunkRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class WorldRenderer {

    private final ChunkRenderer chunkRenderer = new ChunkRenderer();
    private final BlockSelectionRenderer blockSelectionRenderer = new BlockSelectionRenderer();
    private final SkyboxRenderer skyboxRenderer = new SkyboxRenderer();

    private final EntityRenderManagerImpl entityRenderManager = new EntityRenderManagerImpl();

    private LightManager lightManager;
    private Material material;

    private ObservableValue<ShaderProgram> worldShader;
    private ObservableValue<ShaderProgram> entityShader;
    private FrameBuffer frameBuffer;
    private FrameBuffer frameBufferMultisampled;
    private final DefaultFBOWrapper defaultFBO = new DefaultFBOWrapper();
    private ObservableValue<ShaderProgram> frameBufferSP;
    private FrameBufferShadow frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ObservableValue<ShaderProgram> shadowShader;

    private RenderManager context;

    public void init(RenderManager context) {
        this.context = context;

        chunkRenderer.init(context, this);
        blockSelectionRenderer.init(context);
        entityRenderManager.init(context);
        skyboxRenderer.init(context);

        lightManager = new LightManager();
        lightManager.getDirectionalLights().add(new DirectionalLight()
                .setDirection(new Vector3f(-0.15f, -1f, -0.35f))
                .setAmbient(new Vector3f(0.4f))
                .setDiffuse(new Vector3f(1f))
                .setSpecular(new Vector3f(1f)));

        material = new Material().setAmbientColor(new Vector3f(0.5f))
                .setDiffuseColor(new Vector3f(1.0f))
                .setSpecularColor(new Vector3f(1.0f)).setShininess(32f);

        worldShader = ShaderManager.instance().registerShader("world_shader", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/world.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/world.frag")));
        entityShader = ShaderManager.instance().registerShader("entity_shader", new ShaderProgramBuilder()
                .addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/entity.vert"))
                .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/entity.frag")));

        frameBuffer = new FrameBuffer();
        frameBuffer.createFrameBuffer();
        frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBufferMultisampled = new FrameBufferMultiSampled();
        frameBufferMultisampled.createFrameBuffer();
        frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBuffer.check();
        frameBufferMultisampled.check();
        frameBufferSP = ShaderManager.instance().registerShader("frame_buffer_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/framebuffer.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/framebuffer.frag")));
        //TODO init Client shader in a formal way
        frameBufferShadow = new FrameBufferShadow();
        frameBufferShadow.createFrameBuffer();
        shadowShader = ShaderManager.instance().registerShader("shadow_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/shadow.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/shadow.frag")));
    }

    public void render(float partial) {
        // shadow
        glDisable(GL_MULTISAMPLE);
        frameBufferShadow.bind();
        GL11.glViewport(0, 0, FrameBufferShadow.SHADOW_WIDTH, FrameBufferShadow.SHADOW_HEIGHT);
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
        frameBufferShadow.unbind();

        // render chunk
        GL11.glViewport(0, 0, context.getWindow().getWidth(), context.getWindow().getHeight());

        frameBufferMultisampled.bind();
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
        GL11.glBindTexture(GL_TEXTURE_2D, frameBufferShadow.getDstexPointer());
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        chunkRenderer.render();

        // render world
        ShaderManager.instance().bindShader(worldShader.getValue());
        ShaderManager.instance().setUniform("u_ProjMatrix", context.getWindow().projection());
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
        frameBuffer.blitFrom(frameBufferMultisampled);
        defaultFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderManager.instance().bindShader(frameBufferSP.getValue());
        glDisable(GL_DEPTH_TEST);
        defaultFBO.drawFrameBuffer(frameBuffer);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_MULTISAMPLE);
        if (context.getWindow().isResized()) {
            frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
            frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        }
    }

    private void renderEntity(Matrix4f lightSpaceMat, float partial) {
        ShaderManager.instance().bindShader(entityShader.getValue());
        ShaderManager.instance().setUniform("u_ProjMatrix", context.getWindow().projection());
        ShaderManager.instance().setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        ShaderManager.instance().setUniform("u_LightSpace", lightSpaceMat);
        ShaderManager.instance().setUniform("u_ShadowMap", 8);
        lightManager.bind(context.getCamera());
        material.bind("material");
        context.getEngine().getCurrentGame().getWorld().getEntities().forEach(entity ->
                entityRenderManager.render(entity, partial));
    }

    public void dispose() {
        chunkRenderer.dispose();
        blockSelectionRenderer.dispose();
        entityRenderManager.dispose();

        frameBuffer.dispose();
        frameBufferMultisampled.dispose();
        frameBufferShadow.dispose();
        defaultFBO.dispose();

        ShaderManager.instance().unregisterShader("world_shader");
        ShaderManager.instance().unregisterShader("frame_buffer_shader");
        ShaderManager.instance().unregisterShader("shadow_shader");
    }

    public LightManager getLightManager() {
        return lightManager;
    }

    public Material getMaterial() {
        return material;
    }
}