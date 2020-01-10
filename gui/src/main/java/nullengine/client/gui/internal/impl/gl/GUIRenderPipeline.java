package nullengine.client.gui.internal.impl.gl;

import nullengine.client.gui.Scene;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.management.RenderPipeline;

import java.util.Map;

public class GUIRenderPipeline implements RenderPipeline {

    private final Map<Window, Scene> boundWindows;

    private RenderManager manager;
    private GLGUIRenderer renderer;

    public GUIRenderPipeline(Map<Window, Scene> boundWindows) {
        this.boundWindows = boundWindows;
    }

    @Override
    public void init(RenderManager manager) {
        this.manager = manager;
        this.renderer = new GLGUIRenderer();
    }

    @Override
    public void render(float tpf) {
        for (var entry : boundWindows.entrySet()) {
            Window window = entry.getKey();
            Scene scene = entry.getValue();
            if (window.isResized()) {
                scene.setViewport(window.getWidth(), window.getHeight(), window.getContentScaleX(), window.getContentScaleY());
                scene.update();
            }
            renderer.render(scene);
            window.swapBuffers();
        }
    }
}
