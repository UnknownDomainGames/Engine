package unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.client.display.CameraDefault;

public class RendererGlobal {
    private final List<Renderer> renderers = new ArrayList<>();
    private Camera camera = new CameraDefault();

    public RendererGlobal() {
        renderers.add(new RenderCommon(camera));
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    public RendererGlobal add(Renderer renderer) {
        renderers.add(renderer);
        return this;
    }

    public void render() {
        for (Renderer renderer : renderers) {
            renderer.render();
        }
    }

}
