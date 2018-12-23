package unknowndomain.engine.client.rendering;

import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManagerImpl;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.GameContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RenderContextImpl implements RenderContext {

    private final Thread renderThread;
    @Deprecated
    private final List<Renderer.Factory> factories;
    private final List<Renderer> renderers = new ArrayList<>();
    private final GameWindow window;
    private final TextureManager textureManager = new TextureManagerImpl();

    private Camera camera;
    private double partialTick;

    public RenderContextImpl(Thread renderThread, List<Renderer.Factory> factories, GameWindow window) {
        this.renderThread = renderThread;
        this.factories = factories;
        this.window = window;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public GameWindow getWindow() {
        return window;
    }

    @Override
    public TextureManager getTextureManager() {
        return textureManager;
    }

    public void build(GameContext context, ResourceManager resourceManager) {
        this.renderers.clear();
        for (Renderer.Factory factory : factories) {
            try {
                Renderer renderer = factory.create(context, resourceManager);
                renderer.init(this);
                this.renderers.add(renderer);
            } catch (IOException e) {
                // TODO: warning
                e.printStackTrace();
            }
        }
    }

    public void render(double partial) {
        this.partialTick = partial;
        for (Renderer renderer : renderers) {
            renderer.render();
        }
    }

    public void dispose() {
        for (Renderer renderer : renderers) {
            renderer.dispose();
        }
    }

    @Override
    public double partialTick() {
        return partialTick;
    }

    @Override
    public Thread getRenderThread() {
        return renderThread;
    }
}
