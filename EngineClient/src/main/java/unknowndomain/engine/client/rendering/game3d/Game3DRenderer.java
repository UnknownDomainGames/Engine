package unknowndomain.engine.client.rendering.game3d;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.item.ItemRenderManager;
import unknowndomain.engine.client.rendering.item.ItemRenderManagerImpl;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.engine.GameStartEvent;
import unknowndomain.engine.event.engine.GameTerminationEvent;

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
        worldRenderer = new WorldRenderer();
        worldRenderer.init(context);

        itemRenderManager = new ItemRenderManagerImpl();
        itemRenderManager.init(context);
        context.setComponent(ItemRenderManager.class, itemRenderManager);

        context.getEngine().getAssetManager().reload();
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeGameRender();
    }
}
