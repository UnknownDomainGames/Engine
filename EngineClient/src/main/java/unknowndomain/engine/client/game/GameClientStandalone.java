package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionBuilderImpl;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.ActionManagerImpl;
import unknowndomain.engine.client.DefaultGameMode;
import unknowndomain.engine.client.input.keybinding.KeyBindingManager;
import unknowndomain.engine.client.input.keybinding.Keybindings;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.client.rendering.camera.CameraController;
import unknowndomain.engine.client.rendering.camera.CameraDefault;
import unknowndomain.engine.client.rendering.camera.FirstPersonController;
import unknowndomain.engine.client.rendering.display.Camera;
import unknowndomain.engine.client.rendering.display.DefaultGameWindow;
import unknowndomain.engine.client.rendering.gui.RendererGui;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.resource.*;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.TwoHands;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.event.registry.ResourceSetupEvent;
import unknowndomain.engine.game.GameServerFullAsync;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.math.FixStepTicker;
import unknowndomain.engine.mod.ModRepository;
import unknowndomain.engine.mod.ModStore;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class GameClientStandalone extends GameServerFullAsync {
    private RendererContext gameRenderer;
    private ResourceManager resourceManager;
    private ActionManagerImpl actionManager;

    private KeyBindingManager keyBindingManager;
    private CameraController cameraController;
    private MotionController motionController;

    private Camera camera;

    private WorldCommon world;
    private Player player;
    private FixStepTicker.Dynamic ticker;

    private boolean closed;
    private DefaultGameWindow window;

    public GameClientStandalone(Option option, ModRepository repository, ModStore store, EventBus bus, DefaultGameWindow window) {
        super(option, repository, store, bus);
        this.window = window;
        this.ticker = new FixStepTicker.Dynamic(this::clientTick, this::renderTick, 60);

        // TODO:
        bus.register(new DefaultGameMode());
    }


    /**
     * Get player client
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the client world
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

    public CameraController getController() {
        return cameraController;
    }

    @Override
    protected void constructStage() {
        super.constructStage();
        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());
    }

    @Override
    protected void registerStage() {
        motionController = new MotionController();

        super.registerStage();

        actionManager = new ActionManagerImpl(context, this.context.getRegistry().getRegistry(Action.class));
        keyBindingManager = new KeyBindingManager(actionManager);
        Keybindings.INSTANCE.setup(keyBindingManager); // hardcode setup

        List<Renderer.Factory> factories = Lists.newArrayList();
        ClientRegistryEvent clientRegistryEvent = new ClientRegistryEvent(factories);
        bus.post(clientRegistryEvent);

        // FIXME: Don't initialize renderer at here. It should be initialized in Engine
        factories.add((context, manager) -> {
            Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
            byte[] cache = resource.cache();
            return new RendererGui(
                    (ByteBuffer) ByteBuffer.allocateDirect(cache.length).put(cache).flip(),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(),
                            ShaderType.VERTEX_SHADER),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(),
                            ShaderType.FRAGMENT_SHADER));
        });

        camera = new CameraDefault();
        cameraController = new FirstPersonController(camera);
        gameRenderer = new RendererContext(factories, camera, window);
    }

    @Override
    protected void resourceStage() {
        bus.post(new ResourceSetupEvent(context, resourceManager));
        gameRenderer.build(context, resourceManager);
    }

    @Override
    protected void finishStage() {
        spawnWorld(null);

        world = (WorldCommon) getWorld("default");
        player = world.playerJoin(new Profile(UUID.randomUUID(), 12));
        player.getMountingEntity().getPosition().set(1, 3, 1);

        bus.post(new GameReadyEvent(context));
    }

    @Override
    public void run() {
        super.run();
        ticker.start(); // start to tick
    }

    private void clientTick() {
        // TODO update particle physics here
    }

    /**
     * Actual render call
     *
     * @param partialTic
     */
    private void renderTick(double partialTic) {
        cameraController.update(player.getMountingEntity().getPosition(), player.getMountingEntity().getRotation());
        window.beginDraw();
        this.gameRenderer.render(partialTic);
        window.endDraw();
    }

    // https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/GameEngine.java
    private void gameLoop() {
        long lastTime;
        while (!closed) {
            lastTime = System.currentTimeMillis();

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

    // dirty things below...

    @Listener
    public void reg(RegisterEvent event) {
        for (Action action : buildActions()) {
            event.getRegistry().register(action);
        }
    }

    private List<Action> buildActions() {
        List<Action> list = motionController.getActions();
        list.addAll(Lists.newArrayList(
                ActionBuilderImpl.create("player.mouse.right").setStartHandler((c) -> {
                    Player player = this.player;
                    Camera camera = this.camera;

                    if (player == null) return;
                    Entity mountingEntity = player.getMountingEntity();
                    World world = player.getWorld();

                    if (mountingEntity == null || world == null) return;

                    TwoHands twoHands = mountingEntity.getBehavior(TwoHands.class);
                    if (twoHands == null) return;

                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);

                    Item hand = twoHands.getMainHand();
                    if (hand != null) {
                        if (hit != null) {
                            hand.onUseBlockStart(world, mountingEntity, hand, hit);
                        } else {
                            hand.onUseStart(world, mountingEntity, hand);
                        }
                    }
                    if (hit != null) {
                        if (hit.block.shouldActivated(world, mountingEntity, hit.position, hit.block)) {
                            hit.block.onActivated(world, mountingEntity, hit.position, hit.block);
                        }
                    }
                }).build(),
                ActionBuilderImpl.create("player.mouse.left").setStartHandler((c) -> {
                    Player player = this.player;
                    Camera camera = this.cameraController.getCamera();

                    if (player == null) return;
                    Entity mountingEntity = player.getMountingEntity();
                    World world = player.getWorld();

                    if (mountingEntity == null || world == null) return;

                    TwoHands twoHands = mountingEntity.getBehavior(TwoHands.class);
                    if (twoHands == null) return;

                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);
                    if (twoHands.getMainHand() != null) {
                        if (hit != null) {
                            world.setBlock(hit.position, null);
//                            mainHand.onUseBlockStart(world, this, mainHand, hit);
                        } else {
//                            mainHand.onUseStart(world, this, mainHand);
                        }
                    }
                    if (hit != null) {
                        world.setBlock(hit.position, null);
//                        if (hit.block.shouldActivated(world, this, hit.position, hit.block)) {
//                            hit.block.onActivated(world, this, hit.position, hit.block);
//                        }
                    }
                }).build()
        ));
        return list;
    }

    class MotionController {
        static final int RUNNING = 0, SNEAKING = 1,
                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
                JUMPING = 6;
        float factor = 0.1f;
        private Vector3f movingDirection = new Vector3f();
        private boolean[] phases = new boolean[16];

        List<Action> getActions() {
            return Lists.newArrayList(
                    ActionBuilderImpl.create("player.move.forward").setStartHandler((c) -> accept(FORWARD, true))
                            .setEndHandler((c, i) -> accept(FORWARD, false)).build(),
                    ActionBuilderImpl.create("player.move.backward").setStartHandler((c) -> accept(BACKWARD, true))
                            .setEndHandler((c, i) -> accept(BACKWARD, false)).build(),
                    ActionBuilderImpl.create("player.move.left").setStartHandler((c) -> accept(LEFT, true))
                            .setEndHandler((c, i) -> accept(LEFT, false)).build(),
                    ActionBuilderImpl.create("player.move.right").setStartHandler((c) -> accept(RIGHT, true))
                            .setEndHandler((c, i) -> accept(RIGHT, false)).build(),
                    ActionBuilderImpl.create("player.move.jump").setStartHandler((c) -> accept(JUMPING, true))
                            .setEndHandler((c, i) -> accept(JUMPING, false)).build(),
                    ActionBuilderImpl.create("player.move.sneak").setStartHandler((c) -> accept(SNEAKING, true))
                            .setEndHandler((c, i) -> accept(SNEAKING, false)).build()
            );
        }

        private void accept(int action, boolean phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = this.movingDirection;
            switch (action) {
                case FORWARD:
                case BACKWARD:
                    if (this.phases[FORWARD] == this.phases[BACKWARD]) {
                        movingDirection.z = 0;
                    }
                    if (this.phases[FORWARD]) movingDirection.z = -factor;
                    else if (this.phases[BACKWARD]) movingDirection.z = +factor;
                    break;
                case LEFT:
                case RIGHT:
                    if (this.phases[LEFT] == this.phases[RIGHT]) {
                        movingDirection.x = 0;
                    }
                    if (this.phases[RIGHT]) movingDirection.x = factor;
                    else if (this.phases[LEFT]) movingDirection.x = -factor;
                    break;
                case RUNNING:
                    break;
                case JUMPING:
                case SNEAKING:
                    if (this.phases[JUMPING] && !this.phases[SNEAKING]) {
                        movingDirection.y = factor;
                    } else if (this.phases[SNEAKING] && !this.phases[JUMPING]) {
                        movingDirection.y = -factor;
                    } else {
                        movingDirection.y = 0;
                    }
                    break;
            }
            Vector3f f = camera.getFrontVector();
            f.y = 0;
            movingDirection.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), f),
                    player.getMountingEntity().getMotion());
        }
    }
}