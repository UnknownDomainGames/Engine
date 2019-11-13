package nullengine.client.rendering.gui;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetTypes;
import nullengine.client.asset.AssetURL;
import nullengine.client.gui.Container;
import nullengine.client.gui.GuiManager;
import nullengine.client.gui.Scene;
import nullengine.client.gui.internal.Internal;
import nullengine.client.gui.rendering.Graphics;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Renderer;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.gl.shader.ShaderProgram;
import nullengine.client.rendering.gl.shader.ShaderType;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class GuiRenderer implements Renderer {

    private RenderManager context;
    private Window window;
    private GuiManager guiManager;

    private ObservableValue<ShaderProgram> shader;

    private Graphics graphics;

    @Override
    public void init(RenderManager context) {
        this.context = context;
        this.guiManager = context.getGuiManager();
        this.window = context.getWindow();

        shader = ShaderManager.instance().registerShader("gui_shader",
                new ShaderProgramBuilder().addShader(ShaderType.VERTEX_SHADER, AssetURL.of("engine", "shader/gui.vert"))
                        .addShader(ShaderType.FRAGMENT_SHADER, AssetURL.of("engine", "shader/gui.frag")));

        this.graphics = new GraphicsImpl(context, this);
        graphics.setFont(FontHelper.instance().getDefaultFont());

        var assetManager = context.getEngine().getAssetManager();
        Internal.setContext(() -> path -> assetManager.loadDirect(AssetTypes.TEXTURE, path));
    }

    @Override
    public void render(float partial) {
        startRender();

        // render scene
        if (guiManager.isHudVisible() && !guiManager.isDisplayingScreen()) {
            for (Scene scene : guiManager.getDisplayingHuds().values()) {
                startRenderFlag();
                renderScene(scene);
            }
        }
        if (guiManager.isDisplayingScreen() && guiManager.getDisplayingScreen().getRoot().closeRequired()) {
            guiManager.getDisplayingScreen().getRoot().doClosing(guiManager);
        }
        if (guiManager.isDisplayingScreen()) {
            startRenderFlag();
            renderScene(guiManager.getDisplayingScreen());
        }

        endRender();
    }

    public void setClipRect(Vector4fc clipRect) {
        ShaderManager.instance().setUniform("u_ClipRect", clipRect);
    }

    private void startRender() {
        if (window.isResized()) {
            glViewport(0, 0, window.getFrameBufferWidth(), window.getFrameBufferHeight());
        }

        ShaderManager.instance().bindShader(shader.getValue());

        startRenderFlag();

        int width = window.getFrameBufferWidth(), height = window.getFrameBufferHeight();
        ShaderManager.instance().setUniform("u_ProjMatrix", new Matrix4f().setOrtho(0, width, height, 0, 1000, -1000));
        ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f());
        ShaderManager.instance().setUniform("u_WindowSize", new Vector2f(width, height));
        ShaderManager.instance().setUniform("u_ClipRect", new Vector4f(0, 0, width, height));

        context.getTextureManager().getWhiteTexture().bind();
    }

    private void startRenderFlag() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        // GL_POLYGON_SMOOTH will cause transparent lines on objects
//        glEnable(GL_POLYGON_SMOOTH);
//        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    }

    private void endRender() {
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
//        glDisable(GL_POLYGON_SMOOTH);
    }

    private void renderScene(Scene scene) {
        if (window.isResized() || window.getFrameBufferWidth() != scene.getWidth() || window.getFrameBufferHeight() != scene.getHeight()) {
            scene.setSize(window.getFrameBufferWidth(), window.getFrameBufferHeight());
        }

        scene.update();

        Container root = scene.getRoot();
        if (!root.visible().get()) {
            return;
        }

        graphics.pushClipRect(0, 0, scene.width().get(), scene.height().get());
        root.getRenderer().render(root, graphics, context);
        graphics.popClipRect();
    }

//    private void debug(RenderContext context) {
//        graphics.setColor(Color.WHITE);
//
//        // TODO: CrossHair, move it.
//        int middleX = context.getWindow().getWidth() / 2, middleY = context.getWindow().getHeight() / 2;
//        graphics.drawLine(middleX, middleY - 10, middleX, middleY + 10);
//        graphics.drawLine(middleX - 10, middleY, middleX + 10, middleY);
//    }

    @Override
    public void dispose() {
        ShaderManager.instance().unregisterShader("gui_shader");
    }
}
