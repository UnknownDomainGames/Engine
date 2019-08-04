package nullengine.client.rendering.game3d;

import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.Renderer;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.item.ItemRenderManagerImpl;
import nullengine.client.rendering.world.WorldRenderer;
import nullengine.event.Listener;
import nullengine.event.game.GameStartEvent;
import nullengine.event.game.GameTerminationEvent;

public class Game3DRenderer implements Renderer {

    private WorldRenderer worldRenderer;

    private ItemRenderManagerImpl itemRenderManager;

    private RenderContext context;

    @Override
    public void init(RenderContext context) {
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
        if (worldRenderer != null) {
            worldRenderer.dispose();
            worldRenderer = null;

            itemRenderManager.dispose();
            itemRenderManager = null;
        }
    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        if(context.getEngine().getCurrentGame().getWorld() != null) {
            worldRenderer = new WorldRenderer();
            worldRenderer.init(context);
        }
        itemRenderManager = new ItemRenderManagerImpl();
        itemRenderManager.init(context);
        context.setComponent(ItemRenderManager.class, itemRenderManager);

        try {
            context.getEngine().getAssetManager().reload();
        } catch (InterruptedException ignored) {
        }
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeGameRender();
    }

    public RenderContext getContext() {
        return context;
    }
}
