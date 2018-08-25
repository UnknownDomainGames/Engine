package unknowndomain.engine.client.rendering;


import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.display.Projection;
import unknowndomain.engine.client.resource.ResourceManager;

import java.io.IOException;

public interface Renderer {
    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();

        Projection getProjection();

        double partialTick();
    }

    interface Factory {
        Renderer create(GameContext context, ResourceManager resourceManager) throws IOException;
    }
}
