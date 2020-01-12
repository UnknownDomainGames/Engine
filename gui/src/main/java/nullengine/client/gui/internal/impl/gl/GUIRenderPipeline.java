package nullengine.client.gui.internal.impl.gl;

import com.github.mouse0w0.observable.collection.ObservableList;
import nullengine.client.gui.Scene;
import nullengine.client.gui.Stage;
import nullengine.client.gui.internal.StageHelper;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.management.RenderPipeline;

public class GUIRenderPipeline implements RenderPipeline {

    private final ObservableList<Stage> stages = Stage.getStages();

    private RenderManager manager;
    private GLGUIRenderer renderer;

    public GUIRenderPipeline() {
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

            Window window = StageHelper.getWindow(stage);
            if (window.isResized()) {
                scene.setViewport(window.getWidth(), window.getHeight(), window.getContentScaleX(), window.getContentScaleY());
            }

            scene.update();
            renderer.render(scene);
            window.swapBuffers();
        }
    }
}
