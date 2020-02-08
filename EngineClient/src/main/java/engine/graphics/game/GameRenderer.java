package engine.graphics.game;

import engine.client.game.GameClient;
import engine.graphics.RenderManager;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.graphics.world.WorldRenderer;
import engine.graphics.viewport.Viewport;
import engine.event.Listener;
import engine.event.Order;
import engine.event.game.GameStartEvent;
import engine.event.game.GameTerminationEvent;
import engine.event.player.PlayerControlEntityEvent;
import engine.world.World;

public final class GameRenderer {

    private final RenderManager manager;
    private final Viewport viewport;
    private GameClient game;

    private WorldRenderer worldRenderer;

    public GameRenderer(RenderManager manager) {
        this.manager = manager;
        this.viewport = manager.getViewport();
        this.game = manager.getEngine().getCurrentGame();
        manager.getEngine().getEventBus().register(this);
    }

    public void render(float tpf) {
        if (worldRenderer == null) return;
        game.getClientPlayer().getEntityController().updateCamera(viewport.getCamera(), tpf);
        worldRenderer.render(tpf);
    }

    public void dispose() {
        disposeWorldRenderer();
    }

    private void disposeWorldRenderer() {
        if (worldRenderer == null) return;
        worldRenderer.dispose();
        worldRenderer = null;
    }

    @Listener(order = Order.LAST)
    public void onControlEntity(PlayerControlEntityEvent.Post event) {
        World world = event.getNewEntity().getWorld();
        if (worldRenderer != null && worldRenderer.getWorld().equals(world)) {
            return;
        }
        if (worldRenderer == null) {
            VoxelRenderHelper.initialize(manager);
        }
        disposeWorldRenderer();
        worldRenderer = new WorldRenderer(manager, world);
        manager.getEngine().getAssetManager().reload();
    }

    @Listener(order = Order.LAST)
    public void onGameStart(GameStartEvent.Post event) {
        if (event.getGame() instanceof GameClient) {
            game = (GameClient) event.getGame();
        }
    }

    @Listener(order = Order.LAST)
    public void onGameStart(GameTerminationEvent.Post event) {
        if (event.getGame() instanceof GameClient) {
            game = null;
        }
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeWorldRenderer();
    }
}
