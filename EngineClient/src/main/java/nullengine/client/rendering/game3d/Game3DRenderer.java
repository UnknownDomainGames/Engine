package nullengine.client.rendering.game3d;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Renderer;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.block.BlockRenderManagerImpl;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.item.ItemRenderManagerImpl;
import nullengine.client.rendering.world.WorldRenderer;
import nullengine.event.Listener;
import nullengine.event.Order;
import nullengine.event.game.GameTerminationEvent;
import nullengine.event.player.PlayerControlEntityEvent;

public class Game3DRenderer implements Renderer {

    private final Scene scene = new Scene();

    private RenderManager context;

    private ItemRenderManagerImpl itemRenderManager;
    private BlockRenderManagerImpl blockRenderManager;

    private WorldRenderer worldRenderer;

    @Override
    public void init(RenderManager context) {
        this.context = context;

        context.getEngine().getEventBus().register(this);
    }

    @Override
    public void render(float partial) {
        if (worldRenderer == null) {
            return;
        }

        worldRenderer.render(partial);
    }

    @Override
    public void dispose() {
        disposeGameRender();
    }

    private void disposeGameRender() {
        if (worldRenderer == null) {
            return;
        }

        scene.reset();

        blockRenderManager.dispose();
        blockRenderManager = null;

        itemRenderManager.dispose();
        itemRenderManager = null;

        worldRenderer.dispose();
        worldRenderer = null;
    }

    @Listener(order = Order.LAST)
    public void onControlEntity(PlayerControlEntityEvent.Post event) {
        if (scene.isEqualsWorld(event.getNewEntity().getWorld())) {
            return;
        }

        disposeGameRender();
        this.scene.setWorld(event.getNewEntity().getWorld());

        blockRenderManager = new BlockRenderManagerImpl();
        blockRenderManager.init();
        BlockRenderManager.Internal.setInstance(blockRenderManager);

        itemRenderManager = new ItemRenderManagerImpl();
        itemRenderManager.init(context);
        ItemRenderManager.Internal.setInstance(itemRenderManager);

        worldRenderer = new WorldRenderer();
        worldRenderer.init(context, scene);

        context.getEngine().getAssetManager().reload();
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeGameRender();
    }

    public RenderManager getContext() {
        return context;
    }
}
