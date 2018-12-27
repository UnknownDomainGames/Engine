package unknowndomain.engine.client.game;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManagerImpl;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientContextImpl implements ClientContext {

    private final Thread renderThread;
    @Deprecated
    private final List<Renderer.Factory> factories;
    private final List<Renderer> renderers = new ArrayList<>();
    private final GameWindow window;
    private final TextureManager textureManager = new TextureManagerImpl();
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Player player;

    private Camera camera;
    private BlockPrototype.Hit hit;
    private double partialTick;

    public ClientContextImpl(Thread renderThread, List<Renderer.Factory> factories, GameWindow window, Player player) {
        this.renderThread = renderThread;
        this.factories = factories;
        this.window = window;
        this.player = player;
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

    @Override
    public double partialTick() {
        return partialTick;
    }

    @Override
    public Thread getRenderThread() {
        return renderThread;
    }

    @Override
    public FrustumIntersection getFrustumIntersection() {
        return frustumIntersection;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public BlockPrototype.Hit getHit() {
        return hit;
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
        getFrustumIntersection().set(getWindow().projection().mul(getCamera().view((float) partial), new Matrix4f()));
        hit = getPlayer().getWorld().raycast(getCamera().getPosition((float) partialTick()),
                getCamera().getFrontVector((float) partialTick()), 10);
        for (Renderer renderer : renderers) {
            renderer.render();
        }
    }

    public void dispose() {
        for (Renderer renderer : renderers) {
            renderer.dispose();
        }
    }
}
