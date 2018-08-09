package unknowndomain.engine.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.Engine;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.block.PlayerClient;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.keybinding.Keybindings;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.model.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.model.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.model.pipeline.ResolveTextureUVNode;
import unknowndomain.engine.client.rendering.RenderDebug;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.rendering.shader.CreateShaderNode;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.Timer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.SimpleIdentifiedRegistry;
import unknowndomain.engine.registry.SimpleRegistryManager;
import unknowndomain.engine.unclassified.BlockObjectBuilder;
import unknowndomain.engine.world.LogicWorld;

import java.util.List;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private DefaultGameWindow window;
    private RendererGlobal renderer;

    /*
     * Managers section
     */

    private ResourceManagerImpl resourceManager;
    private KeyBindingManager keyBindingManager;
    private ActionManagerImpl actionManager;

    // private GameClientImpl game;
    private LogicWorld world;

    private Timer timer;
    private PlayerClient player;
    private GameContext context;
    private RenderDebug debug;

    EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());
    }

    public GameContext getContext() {
        return context;
    }

    public PlayerClient getPlayer() {
        return player;
    }

    public LogicWorld getWorld() {
        return world;
    }

    private void setupContext() {
        SimpleRegistryManager registryManager = new SimpleRegistryManager(
                ImmutableMap.<Class<?>, Registry<?>>builder().put(BlockObject.class, new SimpleIdentifiedRegistry<>())
                        .build()
        );
        context = new GameContext(registryManager, new AsmEventBus());
        actionManager = new ActionManagerImpl(context);
    }

    private void setupRenderer() {
        renderer = new RendererGlobal();
        renderer.add(new RenderDebug());

        renderer.init(this.resourceManager);
    }

    public void init() {
        window.init();

        setupContext();
        initBlocks();
        world = new LogicWorld(context);

        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        keyBindingManager = new KeyBindingManager();
        setupRenderer();

        player = new PlayerClient(renderer.getCamera());
        for (Action action : player.getActions()) {
            actionManager.register(action);
        }

        Keybindings.INSTANCE.setup(keyBindingManager);

        resourceManager.add("BlockModels", new ResolveModelsNode(), new ResolveTextureUVNode(), new ModelToMeshNode(),
                new MeshToGLNode()).subscribe("BlockModels", resourceManager);
        resourceManager.subscribe("TextureMap", resourceManager);
        resourceManager.add("Shader", new CreateShaderNode());

        timer = new Timer();
        timer.init();
    }

    private void initBlocks() {
       
    }

    public void loop() {

    }

    public void terminate() {

    }

    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / 30.0f;
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                // update(interval); //TODO: game logic
                world.tick();
                // game.tick();
                accumulator -= interval;
            }

            window.update();
            sync();
        }
    }

    private void sync() {
        float loopSlot = 1f / 60.0f;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    boolean paused = false;

    public void handleCursorMove(double x, double y) {
        if (!paused)
            renderer.getCamera().rotate((float) x, (float) y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                getKeyBindingManager().handlePress(key, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                getKeyBindingManager().handleRelease(key, modifiers);
                break;
            default:
                break;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            if (paused) {
                window.hideCursor();
                paused = false;
            } else {
                window.showCursor();
                paused = true;
            }
        }
    }

    public void handleTextInput(int codepoint, int modifiers) {
    }

    public void handleMousePress(int button, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                getKeyBindingManager().handlePress(button + 400, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                getKeyBindingManager().handleRelease(button + 400, modifiers);
                break;
            default:
                break;
        }
    }

    public void handleScroll(double xoffset, double yoffset) {
        // renderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }

    public RendererGlobal getRenderer() {
        return renderer;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    @Override
    public ModManager getModManager() {
        return null; // TODO Inject Mod Manager
    }

    @Override
    public ResourceManager getResourcePackManager() {
        return resourceManager;
    }

    @Override
    public ActionManager getActionManager() {
        return this.actionManager;
    }

    @Override
    public Game getGame() {
        return null;
    }
}
