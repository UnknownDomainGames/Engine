package unknowndomain.engine.client.rendering;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.util.Disposable;

import java.util.LinkedList;
import java.util.List;

public class EngineRenderContext implements RenderContext, Disposable {

    private final EngineClient engine;

    private final List<Renderer> renderers = new LinkedList<>();

    private Thread renderThread;

    public EngineRenderContext(EngineClient engine) {
        this.engine = engine;
    }

    @Override
    public EngineClient getEngine() {
        return engine;
    }

    @Override
    public Thread getRenderThread() {
        return renderThread;
    }

    @Override
    public boolean isRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public void render(double partial) {
        Platform.getEngineClient().getWindow().beginRender();
        for (Renderer renderer : renderers) {
            renderer.render(partial);
        }
        Platform.getEngineClient().getWindow().endRender();
    }

    public void init(Thread renderThread) {
        this.renderThread = renderThread;

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
