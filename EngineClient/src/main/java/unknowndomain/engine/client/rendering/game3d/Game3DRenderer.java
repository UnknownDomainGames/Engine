package unknowndomain.engine.client.rendering.game3d;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.engine.GameStartEvent;
import unknowndomain.engine.event.engine.GameTerminationEvent;

public class Game3DRenderer implements Renderer {

    private WorldRenderer worldRenderer;

    private RenderContext context;
    private GameClient currentGame;

    @Override
    public void init(RenderContext context) {
        this.context = context;

        context.getEngine().getEventBus().register(this);
    }

    @Override
    public void render(float partial) {
        if (currentGame == null) {
            return;
        }

        worldRenderer.render(partial);
    }

    @Override
    public void dispose() {

    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        currentGame = (GameClient) event.getGame();

        worldRenderer = new WorldRenderer();
        worldRenderer.init(context, currentGame);

        Platform.getEngineClient().getAssetManager().reload();
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        worldRenderer.dispose();
        worldRenderer = null;

        currentGame = null;
    }
}
