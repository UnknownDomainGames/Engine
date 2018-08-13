package unknowndomain.engine.client.rendering;


import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.resource.ResourceManager;

import java.io.IOException;

public interface Renderer {
    void init(ResourceManager resourceManager) throws IOException;

    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();
    }
}
