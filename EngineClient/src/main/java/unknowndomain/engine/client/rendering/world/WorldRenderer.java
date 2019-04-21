package unknowndomain.engine.client.rendering.world;

import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.game3d.Game3DRenderer;
import unknowndomain.engine.client.rendering.item.ItemRenderer;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.shader.ShaderProgramBuilder;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.util.*;
import unknowndomain.engine.client.rendering.world.chunk.ChunkRenderer;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {

    private final ChunkRenderer chunkRenderer = new ChunkRenderer();
    private final ItemRenderer itemRenderer = new ItemRenderer();
    private final BlockSelectionRenderer blockSelectionRenderer = new BlockSelectionRenderer();

    private ObservableValue<ShaderProgram> worldShader;
    private FrameBuffer frameBuffer;
    private FrameBuffer frameBufferMultisampled;
    private final DefaultFBOWrapper defaultFBO = new DefaultFBOWrapper();
    private ObservableValue<ShaderProgram> frameBufferSP;
    private FrameBufferShadow frameBufferShadow; //TODO: move to 3D Renderer!!!
    private ObservableValue<ShaderProgram> shadowShader;

    private Game3DRenderer.GameRenderEnv env;
    private RenderContext context;
    private GameClient game;

    public void init(Game3DRenderer.GameRenderEnv env) {
        this.env = env;
        this.context = env.getContext();
        this.game = env.getGame();
        chunkRenderer.init(env);
        itemRenderer.init(env);
        blockSelectionRenderer.init(env);
//        context.getGame().getContext().register(chunkRenderer);
        worldShader =
                ShaderManager.INSTANCE.registerShader("world_shader",
                        new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "world.vert"))
                                .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "world.frag")));
        frameBuffer = new FrameBuffer();
        frameBuffer.createFrameBuffer();
        frameBuffer.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBufferMultisampled = new FrameBufferMultiSampled();
        frameBufferMultisampled.createFrameBuffer();
        frameBufferMultisampled.resize(context.getWindow().getWidth(), context.getWindow().getHeight());
        frameBuffer.check();
        frameBufferMultisampled.check();
        frameBufferSP = ShaderManager.INSTANCE.registerShader("frame_buffer_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "framebuffer.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "framebuffer.frag")));
        //TODO init Client shader in a formal way
        frameBufferShadow = new FrameBufferShadow();
        frameBufferShadow.createFrameBuffer();
        shadowShader = ShaderManager.INSTANCE.registerShader("shadow_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetPath.of("engine", "shader", "shadow.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetPath.of("engine", "shader", "shadow.frag")));
    }

    public void render(float partial) {
        frameBufferShadow.bind();
        GL11.glViewport(0, 0, FrameBufferShadow.SHADOW_WIDTH, FrameBufferShadow.SHADOW_HEIGHT);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        ShaderProgram shadowShader = this.shadowShader.getValue();
        ShaderManager.INSTANCE.bindShaderOverriding(shadowShader);
        var lightProj = new Matrix4f().ortho(-10f * 2, 10f * 2, -10f * 2, 10f * 2, 1.0f / 2, 7.5f * 2);

        var lightView = new Matrix4f().lookAt(new Vector3f(-0.15f, -1f, -0.35f).negate().mul(8).add(0, 5, 0), new Vector3f(0, 5, 0), new Vector3f(0, 1, 0));

        var lightSpaceMat = new Matrix4f();
        lightProj.mul(lightView, lightSpaceMat);

        ShaderManager.INSTANCE.setUniform("u_LightSpace", lightSpaceMat);
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f().setTranslation(0, 0, 0));
        GL11.glCullFace(GL_FRONT);
        chunkRenderer.render();
        GL11.glCullFace(GL_BACK);

        ShaderManager.INSTANCE.unbindOverriding();
        frameBufferShadow.unbind();

        GL11.glViewport(0, 0, context.getWindow().getWidth(), context.getWindow().getHeight());

        frameBufferMultisampled.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        // TODO: Remove it
        ShaderManager.INSTANCE.bindShader("chunk_solid");
        ShaderProgram chunkSolidShader = ShaderManager.INSTANCE.getShader("chunk_solid").getValue();
        if (chunkSolidShader != null) {
            ShaderManager.INSTANCE.setUniform("u_LightSpace", lightSpaceMat);
            ShaderManager.INSTANCE.setUniform("u_ShadowMap", 8);
        }
        GL15.glActiveTexture(GL13.GL_TEXTURE8);
        GL11.glBindTexture(GL_TEXTURE_2D, frameBufferShadow.getDstexid());
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        chunkRenderer.render();

        ShaderManager.INSTANCE.bindShader(worldShader.getValue());

        ShaderManager.INSTANCE.setUniform("u_UsingColor", true);
        ShaderManager.INSTANCE.setUniform("u_UsingTexture", true);
        itemRenderer.render(partial);

        glEnable(GL11.GL_DEPTH_TEST);
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderManager.INSTANCE.setUniform("u_ProjMatrix", context.getWindow().projection());
        ShaderManager.INSTANCE.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f());

        // TODO: Remove it
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        ShaderManager.INSTANCE.setUniform("u_UsingColor", true);
        ShaderManager.INSTANCE.setUniform("u_UsingTexture", false);
        buffer.begin(GL11.GL_LINES, true, true, false);
        buffer.pos(0, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(100, 0, 0).color(1, 0, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 100, 0).color(0, 1, 0).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 1).endVertex();
        buffer.pos(0, 0, 100).color(0, 0, 1).endVertex();
        tessellator.draw();

        blockSelectionRenderer.render(partial);

        frameBuffer.bind();
        glEnable(GL_DEPTH_TEST);
        frameBuffer.blitFrom(frameBufferMultisampled);
        defaultFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        ShaderManager.INSTANCE.bindShader(frameBufferSP.getValue());
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
        itemRenderer.dispose();
        blockSelectionRenderer.dispose();

        ShaderManager.INSTANCE.unregisterShader("world_shader");
        ShaderManager.INSTANCE.unregisterShader("frame_buffer_shader");
        ShaderManager.INSTANCE.unregisterShader("shadow_shader");
    }
}