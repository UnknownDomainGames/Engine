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

    private GameRenderEnv env = new GameRenderEnv();

    @Override
    public void init(RenderContext context) {
        this.env.context = context;

        context.getEngine().getEventBus().register(this);
    }

    @Override
    public void render(float partial) {
        if (env.game == null) {
            return;
        }

        worldRenderer.render(partial);
    }

    @Override
    public void dispose() {
        disposeGameRender();
    }

    public void disposeGameRender() {
        if (env.game != null) {
            worldRenderer.dispose();
            worldRenderer = null;

            env.game = null;
        }
    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        env.game = (GameClient) event.getGame();

        worldRenderer = new WorldRenderer();
        worldRenderer.init(env);

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
