package unknowndomain.engine.client.rendering;


import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.client.rendering.display.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.util.Disposable;

import java.io.IOException;

/**
 * The base renderer for the game. A Mod need to have this if it want to render new things...
 */
public interface Renderer extends Disposable {

    void render(Context context);

    interface Context {
        Camera getCamera();

        GameWindow getWindow();

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
