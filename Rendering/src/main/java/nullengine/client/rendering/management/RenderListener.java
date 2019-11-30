package nullengine.client.rendering.management;

public interface RenderListener {
    void onPreRender(RenderManager manager);

    void onPreSwapBuffers(RenderManager manager);

    void onPostRender(RenderManager manager);
}
