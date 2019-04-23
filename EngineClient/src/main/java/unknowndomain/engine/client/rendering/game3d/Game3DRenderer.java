package unknowndomain.engine.client.rendering.game3d;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.world.WorldRenderer;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.engine.GameStartEvent;
import unknowndomain.engine.event.engine.GameTerminationEvent;

public class Game3DRenderer implements Renderer {

    private WorldRenderer worldRenderer;

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

    public void disposeGameRender() {
        if (worldRenderer != null) {
            worldRenderer.dispose();
            worldRenderer = null;
        }
    }

    @Listener
    public void onGameStart(GameStartEvent.Post event) {
        worldRenderer = new WorldRenderer();
        worldRenderer.init(context);

        context.getEngine().getAssetManager().reload();
    }

    @Listener
    public void onGameTermination(GameTerminationEvent.Pre event) {
        disposeGameRender();
    }
}
