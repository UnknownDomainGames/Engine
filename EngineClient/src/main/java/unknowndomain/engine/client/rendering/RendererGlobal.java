package unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.RenderingLayer;
import unknowndomain.engine.client.display.CameraDefault;

public class RendererGlobal {

    private final List<RenderingLayer> renderers = new ArrayList<>();
    private Camera camera = new CameraDefault();

    public RendererGlobal() {
        renderers.add(new RenderCommon(camera));
    }

    public Camera getCamera() {
        return camera;
    }

    public List<RenderingLayer> getRenderers() {
        return renderers;
    }

    public void render() {
        for (RenderingLayer renderer : renderers) {
            renderer.render();
        }
    }

}
