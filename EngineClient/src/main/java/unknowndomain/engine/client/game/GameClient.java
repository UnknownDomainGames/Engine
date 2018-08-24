package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;

import unknowndomain.engine.Engine;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.ActionManagerImpl;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.keybinding.Keybindings;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.GameCommon;

import java.io.IOException;
import java.util.List;

public class GameClient extends GameCommon {
    private RendererContext gameRenderer;
    private ResourceManager resourceManager;
    private ActionManagerImpl actionManager;
    private KeyBindingManager keyBindingManager;

    public GameClient(Config config, EventBus bus) {
        super(config, bus);
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
}