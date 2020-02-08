package engine.graphics.management;

public interface RenderHandler {

    void init(GraphicsBackend manager);

    void render(float tpf);

    void dispose();
}
