package nullengine.client.gui.internal.impl.gl;

import nullengine.client.gui.Parent;
import nullengine.client.gui.Popup;
import nullengine.client.gui.Scene;
import nullengine.client.gui.rendering.Graphics;
import nullengine.client.rendering.gl.shader.ShaderManager;
import nullengine.client.rendering.gl.shader.ShaderProgram;
import nullengine.client.rendering.gl.shader.Uniforms;
import nullengine.client.rendering.gl.texture.GLFrameBuffer;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.image.BufferedImage;
import nullengine.client.rendering.texture.Texture2D;
import nullengine.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4fc;

import static nullengine.client.gui.internal.SceneHelper.getViewportHeight;
import static nullengine.client.gui.internal.SceneHelper.getViewportWidth;
import static org.lwjgl.opengl.GL11.*;

public final class GLGUIRenderer {

    private ShaderProgram shader;
    private Graphics graphics;

    private Texture2D whiteTexture;

    private int clipRectLocation;
    private int renderTextLocation;

    public GLGUIRenderer() {
        shader = ShaderManager.load("gui");
        clipRectLocation = shader.getUniformLocation("u_ClipRect");
        renderTextLocation = shader.getUniformLocation("u_RenderText");

        whiteTexture = GLTexture2D.of(new BufferedImage(2, 2, 0xffffffff));

        graphics = new GLGraphics(this);
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void render(Scene scene) {
        render(scene, true);
    }

    public void render(Scene scene, boolean clear) {
        render(scene, GLFrameBuffer.getDefaultFrameBuffer(), clear);
    }

    public void render(Scene scene, GLFrameBuffer frameBuffer, boolean clear) {
        frameBuffer.bind();

        int viewportWidth = getViewportWidth(scene), viewportHeight = getViewportHeight(scene);
        glViewport(0, 0, viewportWidth, viewportHeight);

        if (clear) {
            Color fill = scene.getFill();
            glClearColor(fill.getRed(), fill.getGreen(), fill.getBlue(), fill.getAlpha());
            glClear(GL_COLOR_BUFFER_BIT);
        }

        Parent root = scene.getRoot();
        if (!root.visible().get()) return; // Invisible root, don't need render it.

        startRender(scene);
        root.getRenderer().render(root, graphics);
        for (Popup popup : scene.getPopups()) {
            popup.getRenderer().render(popup, graphics);
        }
        endRender();
    }

    private void startRender(Scene scene) {
        int viewportWidth = getViewportWidth(scene), viewportHeight = getViewportHeight(scene);
        float width = scene.getWidth(), height = scene.getHeight();
        float scaleX = scene.getScaleX(), scaleY = scene.getScaleY();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        // GL_POLYGON_SMOOTH will cause transparent lines on objects
//        glEnable(GL_POLYGON_SMOOTH);
//        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        shader.use();
        shader.setUniform("u_ProjMatrix", new Matrix4f().setOrtho2D(0, viewportWidth, viewportHeight, 0));
        shader.setUniform("u_ModelMatrix", new Matrix4f().scale(scaleX, scaleY, 1));
        shader.setUniform("u_ViewportSize", new Vector2f(viewportWidth, viewportHeight));
        graphics.pushClipRect(0, 0, width, height);

        bindWhiteTexture();
    }

    private void endRender() {
        graphics.popClipRect();

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
//        glDisable(GL_POLYGON_SMOOTH);
    }

    public void bindWhiteTexture() {
        whiteTexture.bind();
    }

    public void setClipRect(Vector4fc vector4fc) {
        Uniforms.setUniform(clipRectLocation, vector4fc);
    }

    public void startRenderText() {
        Uniforms.setUniform(renderTextLocation, true);
    }

    public void endRenderText() {
        Uniforms.setUniform(renderTextLocation, false);
    }

    public void dispose() {
        whiteTexture.dispose();
        shader.dispose();
    }
}
