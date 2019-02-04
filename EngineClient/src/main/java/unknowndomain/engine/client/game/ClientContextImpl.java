package unknowndomain.engine.client.game;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.DefaultAssetManager;
import unknowndomain.engine.client.asset.DefaultAssetSourceManager;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.gui.GuiManager;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientContextImpl implements ClientContext {

    private final GameClientStandalone game;
    private final Thread renderThread;
    @Deprecated
    private final List<Renderer.Factory> factories;
    private final List<Renderer> renderers = new ArrayList<>();
    private final GameWindow window;
    private final GuiManager guiManager;

    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Player player;

    private final DefaultAssetSourceManager assetSourceManager;
    private final AssetManager assetManager;

    private Camera camera;
    private RayTraceBlockHit hit;
    private double partialTick;

    public ClientContextImpl(GameClientStandalone game, Thread renderThread, List<Renderer.Factory> factories, GameWindow window, Player player) {
        this.game = game;
        this.renderThread = renderThread;
        this.factories = factories;
        this.window = window;
        this.player = player;
        this.guiManager = new GuiManager(this);
        this.assetSourceManager = new DefaultAssetSourceManager();
        this.assetSourceManager.getSources().add(game.getEngine().getEngineAssetSource());
        this.assetManager = new DefaultAssetManager(assetSourceManager);
    }

    @Override
    public Game getGame() {
        return game;
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

    private long lastUpdateFps = System.currentTimeMillis();
    private int frameCount = 0;
    private int fps = 0;

    @Override
    public int getFps() {
        return fps;
    }

    public void updateFps() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateFps > 1000) {
            fps = frameCount;
            frameCount = 0; // reset the FPS counter
            lastUpdateFps += 1000; // add one second
        }
        frameCount++;
    }

    @Override
    public TextureManager getTextureManager() {
        return game.getTextureManager();
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
    public RayTraceBlockHit getHit() {
        return hit;
    }

    @Override
    public Registry<ClientBlock> getClientBlockRegistry() {
        return game.getContext().getRegistryManager().getRegistry(ClientBlock.class);
    }

    @Deprecated
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
        updateBlockHit();
        for (Renderer renderer : renderers) {
            renderer.render();
        }
    }

    public void updateBlockHit() {
        getFrustumIntersection().set(getWindow().projection().mul(getCamera().view((float) partialTick), new Matrix4f()));
        hit = getPlayer().getWorld().raycast(getCamera().getPosition((float) partialTick()), getCamera().getFrontVector((float) partialTick()), 10);
    }

    public void dispose() {
        for (Renderer renderer : renderers) {
            renderer.dispose();
        }
    }

    @Override
    public World getClientWorld() {
        return game.getWorld();
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
