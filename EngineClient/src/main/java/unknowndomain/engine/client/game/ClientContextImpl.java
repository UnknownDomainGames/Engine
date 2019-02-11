package unknowndomain.engine.client.game;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import unknowndomain.engine.Platform;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.gui.EngineGuiManager;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.util.List;

public class ClientContextImpl implements ClientContext {

    private final GameClientStandalone game;
    private final List<Renderer> renderers;
    private final GameWindow window;
    private final EngineGuiManager engineGuiManager;

    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Player player;

    private Camera camera;
    private RayTraceBlockHit hit;
    private double partialTick;

    public ClientContextImpl(GameClientStandalone game, List<Renderer> renderers, GameWindow window, Player player) {
        this.game = game;
        this.renderers = renderers;
        this.window = window;
        this.player = player;
        this.engineGuiManager = new EngineGuiManager(Platform.getEngineClient());
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
    public double partialTick() {
        return partialTick;
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

    public void initClient() {
        for (Renderer renderer : renderers) {
            renderer.init(this);
        }
        ShaderManager.INSTANCE.reload();
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

    public EngineGuiManager getEngineGuiManager() {
        return engineGuiManager;
    }
}
