package nullengine.client.gui.internal.impl.gl;

import com.github.mouse0w0.observable.collection.ObservableList;
import nullengine.client.gui.Scene;
import nullengine.client.gui.Stage;
import nullengine.client.gui.internal.SceneHelper;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.management.RenderHandler;
import nullengine.client.rendering.management.RenderManager;

import static nullengine.client.gui.internal.SceneHelper.getViewportHeight;
import static nullengine.client.gui.internal.SceneHelper.getViewportWidth;
import static nullengine.client.gui.internal.StageHelper.getWindow;

public final class GUIRenderHandler implements RenderHandler {

    private final ObservableList<Stage> stages = Stage.getStages();

    private RenderManager manager;
    private GLGUIRenderer renderer;

    public GUIRenderHandler() {
    }

    public GLGUIRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void init(RenderManager manager) {
        this.manager = manager;
        this.renderer = new GLGUIRenderer();
    }

    @Override
    public void render(float tpf) {
        for (var stage : stages) {
            Scene scene = stage.getScene();
            if (scene == null) continue;

            Window window = getWindow(stage);
            if (window.isResized() ||
                    getViewportWidth(scene) != window.getWidth() ||
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
