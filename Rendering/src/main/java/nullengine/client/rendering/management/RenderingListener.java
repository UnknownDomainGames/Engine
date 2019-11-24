package nullengine.client.rendering.management;

public interface RenderingListener {

    void onPreInitialize();

    void onInitialized(RenderingContext manager);

    void onPreRender(RenderingContext manager);

    void onPostRender(RenderingContext manager);

    void onDisposed(RenderingContext manager);
}
