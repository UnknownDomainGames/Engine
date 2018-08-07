package unknowndomain.engine.client.rendering;


import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.resource.ResourceManager;

public interface Renderer {
    void init(ResourceManager resourceManager);

    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();
    }
}
