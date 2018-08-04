package unknowndomain.engine.client.rendering;


import unknowndomain.engine.client.display.Camera;

public interface Renderer {
    void init();

    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();
    }
}
