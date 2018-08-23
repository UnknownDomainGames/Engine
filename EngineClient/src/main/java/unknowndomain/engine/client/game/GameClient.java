package unknowndomain.engine.client.game;

import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.game.GameImpl;

public class GameClient extends GameImpl {
    private RendererContext gameRenderer;

    /**
     * @return the gameRenderer
     */
    public RendererContext getGameRenderer() {
        return gameRenderer;
    }

    public static GameClient create(Camera camera, List<Renderer> rendering) {

    }

}