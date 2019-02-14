package unknowndomain.engine.client.rendering;

import unknowndomain.engine.Platform;
import unknowndomain.engine.util.Disposable;

import java.util.LinkedList;
import java.util.List;

public class EngineRenderContext implements Disposable {

    private final List<Renderer> renderers = new LinkedList<>();

    public void render(double partial) {
        Platform.getEngineClient().getWindow().beginRender();
        for (Renderer renderer : renderers) {
            renderer.render(partial);
        }
        Platform.getEngineClient().getWindow().endRender();
    }

    public void init() {
        for (Renderer renderer : renderers) {
            renderer.init(null);
        }
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    @Override
    public void dispose() {
        renderers.forEach(Disposable::dispose);
    }
}
