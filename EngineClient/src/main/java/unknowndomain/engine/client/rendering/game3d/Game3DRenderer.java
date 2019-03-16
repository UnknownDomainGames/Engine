package unknowndomain.engine.client.rendering.game3d;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.world.WorldRenderer;

public class Game3DRenderer implements Renderer {

    private final WorldRenderer worldRenderer = new WorldRenderer();

    @Override
    public void init(RenderContext context) {
        worldRenderer.init(context);
    }

    @Override
    public void render(float partial) {
        worldRenderer.render(partial);
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
    }
}
