package engine.graphics.management;

@Deprecated
public interface RenderHandler {

    void init(GraphicsBackend manager);

    void render(float tpf);

    void dispose();
}
