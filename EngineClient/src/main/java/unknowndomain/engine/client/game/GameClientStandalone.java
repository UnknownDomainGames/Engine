package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.event.asset.AssetReloadEvent;
import unknowndomain.engine.client.event.game.RendererRegisterEvent;
import unknowndomain.engine.client.input.controller.EntityCameraController;
import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.input.keybinding.KeyBindingManager;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.FirstPersonCamera;
import unknowndomain.engine.event.engine.GameTerminationEvent;
import unknowndomain.engine.game.GameDefinition;
import unknowndomain.engine.game.GameServerFullAsync;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;
import unknowndomain.game.Blocks;
import unknowndomain.game.DefaultGameMode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;
    private final Player player;

    private KeyBindingManager keyBindingManager;
    private EntityController entityController;

    private boolean stopped = false;

    public GameClientStandalone(EngineClient engine, GameDefinition definition, Player player) {
        super(engine, definition);
        this.engineClient = engine;
        this.player = player;
    }

    /**
     * Get player client
     */
    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the client world
     */
    @Nonnull
    @Override
    public World getWorld() {
        return player.getControlledEntity().getWorld();
    }

    @Override
    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    public void setEntityController(EntityController controller) {
        this.entityController = controller;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    @Override
    protected void constructStage() {
        super.constructStage();

        // TODO: Move it
        getEventBus().register(new DefaultGameMode());

//        player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
    }

    @Override
    protected void registerStage() {
        super.registerStage();

        var renderContext = engineClient.getRenderContext();
        var window = renderContext.getWindow();

        logger.info("Loading Client-only stuff!");

        logger.info("Initializing key binding!");
        keyBindingManager = new KeyBindingManager(this, registryManager.getRegistry(KeyBinding.class));
        keyBindingManager.reload();
        window.addKeyCallback(keyBindingManager::handleKey);
        window.addMouseCallback(keyBindingManager::handleMouse);

        List<Renderer> registeredRenderer = Lists.newArrayList();
        eventBus.post(new RendererRegisterEvent(registeredRenderer));

        renderContext.setCamera(new FirstPersonCamera(player));

        eventBus.post(new AssetReloadEvent());
    }

    @Override
    protected void resourceStage() {

    }

    @Override
    protected void finishStage() {
        logger.info("Finishing Game Initialization!");

        // TODO: Remove it
        spawnWorld(null);
        var world = (WorldCommon) getWorld("default");
        world.playerJoin(player);
        player.getControlledEntity().getPosition().set(0, 5, 0);

        entityController = new EntityCameraController(player);
        var window = engineClient.getRenderContext().getWindow();
        window.addCursorCallback((xpos, ypos) -> {
            if (window.getCursor().isHiddenCursor()) {
                entityController.handleCursorMove(xpos, ypos);
            }
        });

        super.finishStage();
        logger.info("Game Ready!");

        // TODO: Remove it
        Random random = new Random();
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                for (int top = 3, y = top; y >= 0; y--) {
                    world.setBlock(BlockPos.of(x, y, z), y == top ? Blocks.GRASS : Blocks.DIRT, null);
                }
            }
        }
//        a = Platform.getEngineClient().getSoundManager().createSoundSource("test sound").position(25,5,0).gain(1.0f).speed(dir);
//        a.setLoop(true);
//        a.assignSound(sound);
//        a.play();
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void terminate() {
        logger.info("Terminating Game!");

        getEventBus().post(new GameTerminationEvent.Pre(this));

        super.terminate();

        getEventBus().post(new GameTerminationEvent.Post(this));

        logger.info("Game terminated.");
    }

    public void clientTick() {
        keyBindingManager.tick();
//        Vector3f d = new Vector3f();
//        a.position(a.getPosition().add(dir.mul(0.05f, d)));
//        var p = a.getPosition();
//        if(Math.abs(p.x) > 20 && Math.signum(p.x) == Math.signum(d.x)) {
//            dir.negate();
//            a.speed(dir);
//        }
        // TODO upload particle physics here
    }
}