package nullengine.client.rendering.management;

public interface SwapBuffersListener {
    void onPreSwapBuffers(RenderManager manager, float partial);
}
