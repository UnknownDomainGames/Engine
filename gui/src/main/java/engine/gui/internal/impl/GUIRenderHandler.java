package engine.gui.internal.impl;

import engine.graphics.display.Window;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.management.RenderHandler;
import engine.gui.Scene;
import engine.gui.Stage;
import engine.gui.internal.SceneHelper;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.gl.GLGUIRenderer;

import java.util.ArrayList;
import java.util.List;

import static engine.gui.internal.SceneHelper.getViewportHeight;
import static engine.gui.internal.SceneHelper.getViewportWidth;

public final class GUIRenderHandler implements RenderHandler {

    private final List<Stage> stages = new ArrayList<>();

    private GraphicsBackend manager;
    private GLGUIRenderer renderer;

    public GUIRenderHandler() {
    }

    public GLGUIRenderer getRenderer() {
        return renderer;
    }

    public void add(Stage stage) {
        stages.add(stage);
    }

    public void remove(Stage stage) {
        stages.remove(stage);
    }

    @Override
    public void init(GraphicsBackend manager) {
        this.manager = manager;
        this.renderer = new GLGUIRenderer();
    }

    @Override
    public void render(float tpf) {
        for (var stage : stages) {
            Scene scene = stage.getScene();
            if (scene == null) continue;

            Window window = StageHelper.getWindow(stage);
            if (getViewportWidth(scene) != window.getWidth() ||
                    getViewportHeight(scene) != window.getHeight()) {
                SceneHelper.setViewport(scene, window.getWidth(), window.getHeight(),
                        window.getContentScaleX(), window.getContentScaleY());
            }

            scene.update();
            renderer.render(scene);
            window.swapBuffers();
        }
    }

    @Override
    public void dispose() {
    }
}
