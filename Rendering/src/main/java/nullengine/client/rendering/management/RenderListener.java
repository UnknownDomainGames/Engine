package nullengine.client.rendering.management;

public interface RenderListener {

    void onPreInitialize();

    void onInitialized(RenderManager manager);

    void onPreRender(RenderManager manager);

    void onPostRender(RenderManager manager);

    void onDisposed(RenderManager manager);
}
