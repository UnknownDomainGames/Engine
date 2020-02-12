package engine.graphics.world;

import com.github.mouse0w0.observable.value.ObservableValue;
import engine.client.asset.AssetURL;
import engine.graphics.RenderManager;
import engine.graphics.Scene3D;
import engine.graphics.entity.EntityRenderManagerImpl;
import engine.graphics.gl.GLStreamedRenderer;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.gl.shader.ShaderType;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.gl.texture.GLSampler;
import engine.graphics.light.DirectionalLight;
import engine.graphics.material.Material;
import engine.graphics.shader.ShaderManager;
import engine.graphics.shader.ShaderProgramBuilder;
import engine.graphics.texture.FilterMode;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.graphics.viewport.Viewport;
import engine.graphics.world.chunk.ChunkRenderer;
import engine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public final class WorldRenderer {

    // TODO: adjustable width and height
    public static final int SHADOW_WIDTH = 2048;
    public static final int SHADOW_HEIGHT = 2048;

    private final RenderManager manager;
    private final Viewport viewport;
    private final Scene3D scene;
    private final World world;

    private final ChunkRenderer chunkRenderer;
    private final BlockSelectionRenderer blockSelectionRenderer;
    private final SkyboxRenderer skyboxRenderer;

    private final EntityRenderManagerImpl entityRenderManager = new EntityRenderManagerImpl();

    @Deprecated
    private final Material material;

    private ObservableValue<ShaderProgram> worldShader;
    private ObservableValue<ShaderProgram> entityShader;
    private GLFrameBuffer frameBuffer;
    private GLFrameBuffer frameBufferMultiSample;
    private ObservableValue<ShaderProgram> frameBufferSP;
    private GLFrameBuffer frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ObservableValue<ShaderProgram> shadowShader;

    public WorldRenderer(RenderManager manager, World world) {
        this.manager = manager;
        this.viewport = manager.getViewport();
        this.scene = viewport.getScene();
        this.world = world;

        chunkRenderer = new ChunkRenderer(manager, world);
        blockSelectionRenderer = new BlockSelectionRenderer(manager);
        entityRenderManager.init(manager);
        skyboxRenderer = new SkyboxRenderer(manager);

        scene.getLightManager().getDirectionalLights().add(new DirectionalLight()
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

        frameBuffer = GLFrameBuffer.createRGB16FDepth24Stencil8FrameBuffer(manager.getWindow().getWidth(), manager.getWindow().getHeight());
        frameBufferMultiSample = GLFrameBuffer.createMultiSampleRGB16FDepth24Stencil8FrameBuffer(manager.getWindow().getWidth(), manager.getWindow().getHeight(), GLSampler.DEFAULT);
        frameBufferSP = ShaderManager.instance().registerShader("frame_buffer_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/framebuffer.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/framebuffer.frag")));
        //TODO init Client shader in a formal way
        frameBufferShadow = GLFrameBuffer.createDepthFrameBuffer(SHADOW_WIDTH, SHADOW_HEIGHT);
        shadowShader = ShaderManager.instance().registerShader("shadow_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/shadow.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/shadow.frag")));
    }

    public World getWorld() {
        return world;
    }

    public void render(float tpf) {
        if (manager.getWindow().isResized()) {
            frameBuffer.resize(manager.getWindow().getWidth(), manager.getWindow().getHeight());
            frameBufferMultiSample.resize(manager.getWindow().getWidth(), manager.getWindow().getHeight());
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
        GL11.glViewport(0, 0, manager.getWindow().getWidth(), manager.getWindow().getHeight());

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
        ShaderManager.instance().setUniform("u_ProjMatrix", viewport.getProjectionMatrix());
        ShaderManager.instance().setUniform("u_ViewMatrix", viewport.getCamera().getViewMatrix());
        ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f());
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        skyboxRenderer.render(tpf);
        blockSelectionRenderer.render(tpf);

        renderEntity(lightSpaceMat, tpf);

        // multi sample
        frameBuffer.bind();
        frameBuffer.copyFrom(frameBufferMultiSample, true, false, false, FilterMode.NEAREST);
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
        t.draw(DrawMode.TRIANGLE_STRIP);
    }

    private void renderEntity(Matrix4f lightSpaceMat, float tpf) {
        ShaderProgram shader = entityShader.getValue();
        ShaderManager.instance().bindShader(shader);
        shader.setUniform("u_ProjMatrix", viewport.getProjectionMatrix());
        shader.setUniform("u_ViewMatrix", viewport.getCamera().getViewMatrix());
        shader.setUniform("u_LightSpace", lightSpaceMat);
        shader.setUniform("u_ShadowMap", 8);
        scene.getLightManager().bind(viewport.getCamera().getPosition(), shader);
        material.bind(shader, "material");
        manager.getEngine().getCurrentGame().getClientWorld().getEntities().forEach(entity ->
                entityRenderManager.render(entity, tpf));
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