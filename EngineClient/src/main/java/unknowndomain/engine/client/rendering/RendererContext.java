package unknowndomain.engine.client.rendering;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.Validate;

import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.resource.ResourceManager;

public class RendererContext implements Renderer.Context, Renderer {
    private final List<Renderer> renderers;
    private final Camera camera;

    public RendererContext(List<Renderer> renderers, Camera camera) {
        this.renderers = renderers;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public void init(ResourceManager manager) throws IOException {
        for (Renderer renderer : renderers) {
            renderer.init(manager);
        }
    }

    public RendererContext add(Renderer renderer) {
        Validate.notNull(renderer);
        renderers.add(renderer);
        return this;
    }

    @Override
    public void render(Context context) {
        for (Renderer renderer : renderers) {
            renderer.render(this);
        }
    }

    @Override
    public void dispose() {
        for (Renderer renderer : renderers) {
            renderer.dispose();
        }
    }
}
