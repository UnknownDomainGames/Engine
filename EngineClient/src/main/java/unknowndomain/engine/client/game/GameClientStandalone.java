package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;

import unknowndomain.engine.Engine;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.ActionManagerImpl;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.keybinding.Keybindings;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.GameCommon;
import unknowndomain.engine.game.GameServer;
import unknowndomain.engine.math.FixStepTicker;
import unknowndomain.engine.math.Timer;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class GameClientStandalone extends GameServer {
    private RendererContext gameRenderer;
    private ResourceManager resourceManager;
    private ActionManagerImpl actionManager;
    private KeyBindingManager keyBindingManager;

    private WorldCommon world;
    private FixStepTicker.Dynamic ticker;

    private boolean closed;
    private DefaultGameWindow window;

    public GameClientStandalone(Config config, EventBus bus, DefaultGameWindow window) {
        super(config, bus);
        this.window = window;
        // this.ticker = new FixStepTicker.Dynamic();
    }

    /**
     * @return the world
     */
    public WorldCommon getWorld() {
        return world;
    }

    /**
     * @return the actionManager
     */
    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * @return the gameRenderer
     */
    public RendererContext getGameRenderer() {
        return gameRenderer;
    }

    /**
     * @return the keyBindingManager
     */
    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    @Override
    protected void constructStage() {
        super.constructStage();
        resourceManager = new ResourceManagerImpl();
        // TODO: collect resource sources
    }

    @Override
    protected void registerStage() {
        super.registerStage();

        actionManager = new ActionManagerImpl(context, this.context.getRegistry().getRegistry(Action.class));
        keyBindingManager = new KeyBindingManager(actionManager);
        Keybindings.INSTANCE.setup(keyBindingManager); // hardcode setup

        List<Renderer> renderers = Lists.newArrayList();
        // TODO: collect renderers
        gameRenderer = new RendererContext(renderers, null);

    }

    @Override
    protected void resourceStage() {
        try {
            gameRenderer.init(resourceManager);
        } catch (IOException e) {
            Engine.getLogger().warn("Catch an error during game construct stage.");
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
    }

    private void updateRenderData() {

    }

    private void render() {
        window.beginDraw();
        this.gameRenderer.render(null);
        window.endDraw();
    }

    // https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/GameEngine.java
    private void gameLoop() {
        long lastTime;
        while (!closed) {
            lastTime = System.currentTimeMillis();

            window.beginDraw();
            this.gameRenderer.render(null);
            window.endDraw();

            long diff = System.currentTimeMillis() - lastTime;
            while (diff < (1000 / 60)) {
                try {
                    Thread.sleep(diff / 2);
                } catch (InterruptedException ie) {
                }
                diff = System.currentTimeMillis() - lastTime;
            }
            // sync();
        }
    }

    // private void sync() {
    // float loopSlot = 1f / 60.0f;
    // double endTime = timer.getLastLoopTime() + loopSlot;
    // while (timer.getTime() < endTime) {
    // try {
    // Thread.sleep(1);
    // } catch (InterruptedException ie) {
    // }
    // }
    // }

    @Override
    public World spawnWorld(unknowndomain.engine.world.World.Config config) {
        return null;
    }
}