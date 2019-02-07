package unknowndomain.engine.client.rendering;


import unknowndomain.engine.client.game.ClientContext;
import unknowndomain.engine.util.Disposable;

/**
 * The base renderer for the game. A Mod need to have this if it want to render new things...
 */
public interface Renderer extends Disposable {

    void init(ClientContext context);

    void render();
}
