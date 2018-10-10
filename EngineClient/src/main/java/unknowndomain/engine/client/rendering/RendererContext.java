package unknowndomain.engine.client.rendering;

import afu.org.checkerframework.checker.oigj.qual.O;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.display.GameWindow;
import unknowndomain.engine.client.display.Projection;
import unknowndomain.engine.client.resource.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RendererContext implements Renderer.Context {
    private final List<Renderer> renderers = new ArrayList<>();
    private final Camera camera;
    private final Projection projection;
    private final List<Renderer.Factory> factories;
    private final GameWindow window;
    private double partialTick;

    public RendererContext(List<Renderer.Factory> factories, Camera camera, Projection projection, GameWindow window) {
        this.factories = factories;
        this.camera = camera;
        this.projection = projection;
        this.window = window;
    }

    public void build(GameContext context, ResourceManager resourceManager) {
        this.renderers.clear();
        for (Renderer.Factory factory : factories) {
            try {
                Renderer renderer = factory.create(context, resourceManager);
                this.renderers.add(renderer);
            } catch (IOException e) {
                // TODO: warning
                e.printStackTrace();
            }
        }
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Projection getProjection() {
        return projection;
    }

    @Override
    public GameWindow getWindow() {
        return window;
    }

    public void render(double partial) {
        this.partialTick = partial;
        for (Renderer renderer : renderers) {
            renderer.render(this);
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
}
