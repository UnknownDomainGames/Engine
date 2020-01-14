package nullengine.client.rendering.management;

public interface RenderHandler {

    void init(RenderManager manager);

    void render(float tpf);

    void dispose();
}
