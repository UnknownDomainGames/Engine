package unknowndomain.engine.client.rendering;


import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.util.Disposable;

import java.io.IOException;

/**
 * The base renderer for the game. A Mod need to have this if it want to render new things...
 */
public interface Renderer extends Disposable {

    void init(RenderContext context);

    void render();

    /**
     * The factory object the mod will need to register.
     * <p>The mod won't directly register the renderer, but register this to parse renderer each time.</p>
     */
    interface Factory {
        Renderer create(GameContext context, ResourceManager resourceManager) throws IOException;
    }
}
