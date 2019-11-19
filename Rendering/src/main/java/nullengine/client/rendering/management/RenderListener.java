package nullengine.client.rendering.management;

public interface RenderListener {

    void onPreInitialize();

    void onInitialized(RenderContext manager);

    void onPreRender(RenderContext manager);

    void onPostRender(RenderContext manager);

    void onDisposed(RenderContext manager);
}
