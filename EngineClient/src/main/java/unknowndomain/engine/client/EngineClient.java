package unknowndomain.engine.client;

import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.Engine;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.keybinding.Keybindings;
import unknowndomain.engine.client.player.FirstPersonController;
import unknowndomain.engine.client.player.PlayerController;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.game.GameImpl;
import unknowndomain.engine.math.Timer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.SimpleModManager;
import unknowndomain.engine.world.World0;

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
    private Timer timer;
    private PlayerController playerController = new FirstPersonController();
    private GameImpl game;
    private EventBus bus;
    private SimpleModManager modManager;

    EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());
    }

    public PlayerController getController() {
        return playerController;
    }

    public World0 getWorld() {
        return null;
//        return world;
    }

    private MinecraftMod minecraftMod = new MinecraftMod();

    public void init() {
        window.init();

        bus = new AsmEventBus();

        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        modManager = new SimpleModManager(bus);
        modManager.getMod("unknowndomain");

        game = new GameImpl();
        game.preInit(bus);

        renderer = new RendererGlobal();
        playerController.setCamera(renderer.getCamera());

        keyBindingManager = new KeyBindingManager();

        Keybindings.INSTANCE.setup(keyBindingManager);

//        minecraftMod.init(context);
//        try {
//            minecraftMod.setupResource(context, resourceManager, renderer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        renderer.init(resourceManager);

        // new
//        Player player = world.playerJoin(new Player.Data(UUID.randomUUID(), 12));
//        playerController.setPlayer(player);
//        for (Action action : playerController.getActions()) {
//            actionManager.register(action);
//        }
//
//        player.getMountingEntity().getPosition().set(1, 2, 1);

//        minecraftMod.postInit(context);

        timer = new Timer();
        timer.init();
    }

    public void loop() {

    }

    public void terminate() {

    }

    // https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/GameEngine.java
    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / 30.0f;
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                // update(interval); //TODO: game logic
                playerController.tick();
                game.tick();
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
