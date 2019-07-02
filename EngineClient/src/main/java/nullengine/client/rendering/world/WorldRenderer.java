package nullengine.client.rendering.world;

import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.asset.AssetPath;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.entity.EntityRenderManagerImpl;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import nullengine.client.rendering.shader.ShaderType;
import nullengine.client.rendering.util.DefaultFBOWrapper;
import nullengine.client.rendering.util.FrameBuffer;
import nullengine.client.rendering.util.FrameBufferMultiSampled;
import nullengine.client.rendering.util.FrameBufferShadow;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.client.rendering.world.chunk.ChunkRenderer;
import nullengine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {

    private final ChunkRenderer chunkRenderer = new ChunkRenderer();
    private final BlockSelectionRenderer blockSelectionRenderer = new BlockSelectionRenderer();
    private final SkyboxRenderer skyboxRenderer = new SkyboxRenderer();

    private final EntityRenderManagerImpl entityRenderManager = new EntityRenderManagerImpl();

    private final MutableValue<World> world = new SimpleMutableObjectValue<>();

    private ObservableValue<ShaderProgram> worldShader;
    private FrameBuffer frameBuffer;
    private FrameBuffer frameBufferMultisampled;
    private final DefaultFBOWrapper defaultFBO = new DefaultFBOWrapper();
    private ObservableValue<ShaderProgram> frameBufferSP;
    private FrameBufferShadow frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ObservableValue<ShaderProgram> shadowShader;

    private RenderContext context;

    public void init(RenderContext context) {
        this.context = context;
        chunkRenderer.init(context);
        blockSelectionRenderer.init(context);
        entityRenderManager.init(context);

        world.setValue(context.getEngine().getCurrentGame().getWorld());

//        context.getGame().getContext().register(chunkRenderer);
        worldShader =
                ShaderManager.registerShader("world_shader",
                        new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "world.vert"))
                                .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "world.frag")));
        skyboxRenderer.init(context);
        frameBuffer = new FrameBuffer();
        frameBuffer.createFrameBuffer();
        frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBufferMultisampled = new FrameBufferMultiSampled();
        frameBufferMultisampled.createFrameBuffer();
        frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBuffer.check();
        frameBufferMultisampled.check();
        frameBufferSP = ShaderManager.registerShader("frame_buffer_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "framebuffer.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "framebuffer.frag")));
        //TODO init Client shader in a formal way
        frameBufferShadow = new FrameBufferShadow();
        frameBufferShadow.createFrameBuffer();
        shadowShader = ShaderManager.registerShader("shadow_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "shadow.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "shadow.frag")));
    }

    public void render(float partial) {
        frameBufferShadow.bind();
        GL11.glViewport(0, 0, FrameBufferShadow.SHADOW_WIDTH, FrameBufferShadow.SHADOW_HEIGHT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        ShaderProgram shadowShader = this.shadowShader.getValue();
        ShaderManager.bindShaderOverriding(shadowShader);
        var lightProj = new Matrix4f().ortho(-10f * 2, 10f * 2, -10f * 2, 10f * 2, 1.0f / 2, 7.5f * 2);

        var lightView = new Matrix4f().lookAt(new Vector3f(-0.15f, -1f, -0.35f).negate().mul(8).add(0, 5, 0), new Vector3f(0, 5, 0), new Vector3f(0, 1, 0));

        var lightSpaceMat = new Matrix4f();
        lightProj.mul(lightView, lightSpaceMat);

        ShaderManager.setUniform("u_LightSpace", lightSpaceMat);
        ShaderManager.setUniform("u_ModelMatrix", new Matrix4f().setTranslation(0, 0, 0));
        GL11.glCullFace(GL_FRONT);
        chunkRenderer.render();
        GL11.glCullFace(GL_BACK);

        ShaderManager.unbindOverriding();
        frameBufferShadow.unbind();

        GL11.glViewport(0, 0, context.getWindow().getWidth(), context.getWindow().getHeight());

        frameBufferMultisampled.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        // TODO: Remove it
        ShaderManager.bindShader("chunk_solid");
        ShaderProgram chunkSolidShader = ShaderManager.getShader("chunk_solid").getValue();
        if (chunkSolidShader != null) {
            ShaderManager.setUniform("u_LightSpace", lightSpaceMat);
            ShaderManager.setUniform("u_ShadowMap", 8);
        }
        GL15.glActiveTexture(GL13.GL_TEXTURE8);
        GL11.glBindTexture(GL_TEXTURE_2D, frameBufferShadow.getDstexid());
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        chunkRenderer.render();

        ShaderManager.bindShader(worldShader.getValue());

        ShaderManager.setUniform("u_ProjMatrix", context.getWindow().projection());
        ShaderManager.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());

        ShaderManager.setUniform("u_ModelMatrix", new Matrix4f());

        // TODO: Support shadow and light. Move it.
        world.getValue().getEntities().forEach(entity -> entityRenderManager.render(entity, partial));

        glEnable(GL11.GL_DEPTH_TEST);
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderManager.setUniform("u_ModelMatrix", new Matrix4f());
        //skyboxRenderer.render(partial);
        // TODO: Remove it
        context.getTextureManager().getWhiteTexture().bind();
        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.LINES, GLBufferFormats.POSITION_COLOR_TEXTURE);
        buffer.pos(0, 0, 0).color(1, 0, 0).uv(0, 0).endVertex();
        buffer.pos(100, 0, 0).color(1, 0, 0).uv(0, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 1, 0).uv(0, 0).endVertex();
        buffer.pos(0, 100, 0).color(0, 1, 0).uv(0, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 1).uv(0, 0).endVertex();
        buffer.pos(0, 0, 100).color(0, 0, 1).uv(0, 0).endVertex();
        tessellator.draw();

        blockSelectionRenderer.render(partial);

        frameBuffer.bind();
        glEnable(GL_DEPTH_TEST);
        frameBuffer.blitFrom(frameBufferMultisampled);
        defaultFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderManager.bindShader(frameBufferSP.getValue());
        glDisable(GL_DEPTH_TEST);
        defaultFBO.drawFrameBuffer(frameBuffer);

        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);

        if (context.getWindow().isResized()) {
            frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
            frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        }
    }

    public void dispose() {
        chunkRenderer.dispose();
        blockSelectionRenderer.dispose();
        entityRenderManager.dispose();

        ShaderManager.unregisterShader("world_shader");
        ShaderManager.unregisterShader("frame_buffer_shader");
        ShaderManager.unregisterShader("shadow_shader");
    }
}