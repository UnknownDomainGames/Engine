package nullengine.client.rendering.management;

public interface RenderPipeline {

    void init(RenderManager manager);

    void render(float tpf);
}
