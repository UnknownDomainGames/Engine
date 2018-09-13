package unknowndomain.engine.client.rendering;


import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.display.Projection;
import unknowndomain.engine.client.resource.ResourceManager;

import java.io.IOException;

/**
 * The base renderer for the game. A Mod need to have this if it want to render new things...
 */
public interface Renderer {
    void render(Context context);

    void dispose();

    interface Context {
        Camera getCamera();

        Projection getProjection();

        double partialTick();
    }

    /**
     * The factory object the mod will need to register.
     * <p>The mod won't directly register the renderer, but register this to parse renderer each time.</p>
     */
    interface Factory {
        Renderer create(GameContext context, ResourceManager resourceManager) throws IOException;
    }
}
