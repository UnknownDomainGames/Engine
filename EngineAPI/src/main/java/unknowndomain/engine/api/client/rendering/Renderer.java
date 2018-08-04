package unknowndomain.engine.api.client.rendering;


import unknowndomain.engine.api.client.display.Camera;

public interface Renderer {
    void init();

    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();
    }
}
