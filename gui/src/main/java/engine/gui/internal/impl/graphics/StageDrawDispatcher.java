package engine.gui.internal.impl.graphics;

import engine.graphics.display.Window;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.shader.ShaderResource;
import engine.gui.Parent;
import engine.gui.Popup;
import engine.gui.Scene;
import engine.gui.Stage;
import engine.gui.internal.SceneHelper;
import engine.gui.internal.StageHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public final class StageDrawDispatcher implements DrawDispatcher {
    private final Stage stage;

    private GraphicsImpl graphics;

    public StageDrawDispatcher(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void init(ShaderResource resource) {
        graphics = new GraphicsImpl(resource);
    }

    @Override
    public void draw(Frame frame, ShaderResource resource, Renderer renderer) {
        Scene scene = stage.getScene();
        if (scene == null) return;

        Window window = StageHelper.getWindow(stage);
        int width = frame.getWidth(), height = frame.getHeight();
        if (frame.isResized()) {
            SceneHelper.setViewport(scene, width, height,
                    window.getContentScaleX(), window.getContentScaleY());
        }
        scene.update();

        Parent root = scene.getRoot();
        if (!root.visible().get()) {
            return; // Invisible root, don't need render it.
        }

        float scaleX = scene.getScaleX(), scaleY = scene.getScaleY();
        graphics.setRenderer(renderer);
        resource.setUniform("u_ProjMatrix", new Matrix4f().setOrtho2D(0, width, height, 0));
        graphics.pushModelMatrix(new Matrix4f().scale(scaleX, scaleY, 1));
        resource.setUniform("u_ViewportSize", new Vector2f(width, height));
        graphics.pushClipRect(0, 0, width / scaleX, height / scaleY);

        root.getRenderer().render(root, graphics);
        for (Popup popup : scene.getPopups()) {
            popup.getRenderer().render(popup, graphics);
        }
    }
}
