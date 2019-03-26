package unknowndomain.engine.client.rendering.game3d;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.engine.GameStartEvent;
import unknowndomain.engine.event.engine.GameTerminationEvent;
import unknowndomain.engine.world.World;

public class Game3DRenderer implements Renderer {

    private WorldRenderer worldRenderer;

    private GameRenderEnv context = new GameRenderEnv();

    @Override
    public void init(RenderContext context) {
        this.context.context = context;

        context.getEngine().getEventBus().register(this);
    }

    @Override
    public void render(float partial) {
        if (context.game == null) {
            return;
        }

        worldRenderer.render(partial);
    }

    @Override
    public void dispose() {
        disposeGameRender();
    }

    public void disposeGameRender() {
        if (context.game != null) {
            worldRenderer.dispose();
            worldRenderer = null;

            context.game = null;
        }
    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        context.game = (GameClient) event.getGame();

        worldRenderer = new WorldRenderer();
        worldRenderer.init(context);

        Platform.getEngineClient().getAssetManager().reload();
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeGameRender();
    }

    public static class GameRenderEnv {
        private RenderContext context;
        private GameClient game;

        private World world;

        public RenderContext getContext() {
            return context;
        }

        public GameClient getGame() {
            return game;
        }

        public World getWorld() {
            return world;
        }
    }
}
