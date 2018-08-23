package unknowndomain.engine.client;

import unknowndomain.engine.Engine;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.player.FirstPersonController;
import unknowndomain.engine.client.player.PlayerController;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.Timer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.SimpleModManager;
import unknowndomain.engine.world.World0;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private DefaultGameWindow window;

    /*
     * Managers section
     */

    private ResourceManagerImpl resourceManager;
    private PlayerController playerController = new FirstPersonController();

    private ActionManagerImpl actionManager;
    private SimpleModManager modManager;
    private Player.Profile playerProfile;

    private Timer timer;
    private EventBus bus;

    private GameClient game;
    private MinecraftMod minecraftMod = new MinecraftMod();

    EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());
    }

    public PlayerController getController() {
        return playerController;
    }


    public World0 getWorld() {
        return null;
        // return world;
    }


    private Game buildGame(Game.Manifest manifest) {

        return null;
    }

    public void init() {
        window.init();

        bus = new AsmEventBus();

        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        // modManager = new SimpleModManager(bus);
        // modManager.getMod("unknowndomain");

        game = new GameClient();
        game.preInit(bus);

        // minecraftMod.init(context);
        // try {
        // minecraftMod.setupResource(context, resourceManager, renderer);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // renderer.init(resourceManager);

        // new
        // Player player = world.playerJoin(new Player.Data(UUID.randomUUID(), 12));
        // playerController.setPlayer(player);
        // for (Action action : playerController.getActions()) {
        // actionManager.register(action);
        // }
        //
        // player.getMountingEntity().getPosition().set(1, 2, 1);

        // minecraftMod.postInit(context);

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

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public ActionManager getActionManager() {
        return this.actionManager;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
