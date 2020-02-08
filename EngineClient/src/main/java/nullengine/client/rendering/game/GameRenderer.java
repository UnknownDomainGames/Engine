package nullengine.client.rendering.game;

import nullengine.client.game.GameClient;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.voxel.VoxelRenderHelper;
import nullengine.client.rendering.world.WorldRenderer;
import nullengine.client.rendering3d.viewport.Viewport;
import nullengine.event.Listener;
import nullengine.event.Order;
import nullengine.event.game.GameStartEvent;
import nullengine.event.game.GameTerminationEvent;
import nullengine.event.player.PlayerControlEntityEvent;
import nullengine.world.World;

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
