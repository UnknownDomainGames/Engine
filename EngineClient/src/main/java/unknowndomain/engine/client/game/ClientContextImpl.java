package unknowndomain.engine.client.game;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.display.Window;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.util.List;

public class ClientContextImpl implements ClientContext {

    private final GameClientStandalone game;
    private final List<Renderer> renderers;
    private final Window window;

    private final FrustumIntersection frustumIntersection = new FrustumIntersection();
    private final Player player;

    private Camera camera;
    private RayTraceBlockHit hit;
    private double partialTick;

    public ClientContextImpl(GameClientStandalone game, List<Renderer> renderers, Window window, Player player) {
        this.game = game;
        this.renderers = renderers;
        this.window = window;
        this.player = player;
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
    public Window getWindow() {
        return window;
    }

    @Override
    public int getFps() {
        return getWindow().getFps();
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
            renderer.init(null);
        }
        ShaderManager.INSTANCE.reload();
    }

    public void render(float partial) {
        this.partialTick = partial;
        updateBlockHit();
        for (Renderer renderer : renderers) {
            renderer.render(partial);
        }
    }

    public void updateBlockHit() {
        getFrustumIntersection().set(getWindow().projection().mul(getCamera().getViewMatrix(), new Matrix4f()));
        hit = getPlayer().getWorld().raycast(getCamera().getPosition(), getCamera().getFrontVector(), 10);
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
}
