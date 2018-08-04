package unknowndomain.engine.client.rendering;

import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.display.CameraDefault;

import java.util.ArrayList;
import java.util.List;

public class RendererGlobal implements Renderer.Context, Pipeline.Endpoint {
    private final List<Renderer> renderers = new ArrayList<>();
    private Camera camera = new CameraDefault();

    public Camera getCamera() {
        return camera;
    }

    public RendererGlobal add(Renderer renderer) {
        renderers.add(renderer);
        renderer.init();
        return this;
    }

    public void render() {
        for (Renderer renderer : renderers) {
            renderer.render(this);
        }
    }

    @Override
    public void accept(String source, Object content) {
//        if (source.equals("Shader") && content instanceof EnumMap) {
//            EnumMap<ShaderType, Shader> map = (EnumMap<ShaderType, Shader>) content;
//            Shader shader = map.get(ShaderType.VERTEX_SHADER);
//        }
//        add((Renderer) content);
    }
}
