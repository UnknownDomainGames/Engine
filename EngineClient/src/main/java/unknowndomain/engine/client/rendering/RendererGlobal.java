package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderType;
import unknowndomain.engine.api.resource.Pipeline;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.client.display.CameraDefault;

import java.util.ArrayList;
import java.util.EnumMap;
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
