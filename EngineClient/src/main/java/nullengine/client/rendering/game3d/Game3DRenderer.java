package nullengine.client.rendering.game3d;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Renderer;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.block.BlockRenderManagerImpl;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.item.ItemRenderManagerImpl;
import nullengine.client.rendering.world.WorldRenderer;
import nullengine.event.Listener;
import nullengine.event.game.GameStartEvent;
import nullengine.event.game.GameTerminationEvent;

public class Game3DRenderer implements Renderer {

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

        blockRenderManager.dispose();
        blockRenderManager = null;

        itemRenderManager.dispose();
        itemRenderManager = null;

        worldRenderer.dispose();
        worldRenderer = null;
    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        blockRenderManager = new BlockRenderManagerImpl();
        blockRenderManager.init();
        BlockRenderManager.Internal.setInstance(blockRenderManager);

        itemRenderManager = new ItemRenderManagerImpl();
        itemRenderManager.init(context);
        ItemRenderManager.Internal.setInstance(itemRenderManager);

        worldRenderer = new WorldRenderer();
        worldRenderer.init(context);

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
