package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionBuilderImpl;
import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.action.ActionManagerImpl;
import unknowndomain.engine.client.input.controller.EntityCameraController;
import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.client.input.controller.MotionType;
import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.input.keybinding.KeyBindingManager;
import unknowndomain.engine.client.input.keybinding.KeyModifier;
import unknowndomain.engine.client.input.keybinding.Keybindings;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.camera.FirstPersonCamera;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.shader.ShaderType;
import unknowndomain.engine.client.rendering.texture.TextureTypes;
import unknowndomain.engine.client.resource.*;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.event.registry.ResourceSetupEvent;
import unknowndomain.engine.game.GameServerFullAsync;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.FixStepTicker;
import unknowndomain.engine.mod.ModRepository;
import unknowndomain.engine.mod.ModStore;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.player.PlayerImpl;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;
import unknowndomain.game.Blocks;
import unknowndomain.game.DefaultGameMode;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameClientStandalone extends GameServerFullAsync {

    private GLFWGameWindow window;
    private ClientContextImpl renderContext;
    private ResourceManager resourceManager;

    private ActionManagerImpl actionManager;
    private KeyBindingManager keyBindingManager;
    private EntityController entityController;

    private WorldCommon world;
    private Player player;

    private FixStepTicker.Dynamic ticker;

    private boolean stopped;

    public GameClientStandalone(Option option, ModRepository repository, ModStore store, EventBus bus, GLFWGameWindow window) {
        super(option, repository, store, bus);
        this.window = window;

        this.ticker = new FixStepTicker.Dynamic(this::clientTick, this::renderTick, FixStepTicker.clientTick);

        // TODO: Remove it
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
    public World getWorld() {
        return world;
    }

    /**
     * @return the actionManager
     */
    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * @return the renderContext
     */
    public ClientContext getRenderContext() {
        return renderContext;
    }

    /**
     * @return the keyBindingManager
     */
    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    protected void constructStage() {
        super.constructStage();
        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
    }

    @Override
    protected void registerStage() {
        super.registerStage();

        // actionManager = new ActionManagerImpl(context,
        // this.context.getRegistry().getRegistry(Action.class));
        // actionManager.registerAll(buildActions().toArray(new Action[0]));
        keyBindingManager = new KeyBindingManager(context, context.getRegistry().getRegistry(KeyBinding.class));
        registerKeyBindings(keyBindingManager);
        // TODO fire KeyBindingRegistryEvent or something

        Keybindings.INSTANCE.setup(keyBindingManager); // TODO: Remove it hardcode setup
        window.addKeyCallback(keyBindingManager::handleKey);
        window.addMouseCallback(keyBindingManager::handleMouse);

        List<Renderer.Factory> factories = Lists.newArrayList();
        ClientRegistryEvent clientRegistryEvent = new ClientRegistryEvent(factories);
        eventBus.post(clientRegistryEvent);

        // FIXME: Don't initialize renderer at here. It should be initialized in Engine
        factories.add((context, manager) -> {
            Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
            byte[] cache = resource.cache();
            return new GuiRenderer(ByteBuffer.allocateDirect(cache.length).put(cache).flip(),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(), ShaderType.VERTEX_SHADER),
                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(), ShaderType.FRAGMENT_SHADER));
        });

        renderContext = new ClientContextImpl(Thread.currentThread(), factories, window, player);
        renderContext.build(context, resourceManager);
        renderContext.setCamera(new FirstPersonCamera(player));
    }

    @Override
    protected void resourceStage() {
        eventBus.post(new ResourceSetupEvent(context, resourceManager, renderContext.getTextureManager()));
        renderContext.getTextureManager().initTextureAtlas(TextureTypes.BLOCK);
    }

    @Override
    protected void finishStage() {
        spawnWorld(null);

        // TODO:
        world = (WorldCommon) getWorld("default");
        world.playerJoin(player);
        player.getControlledEntity().getPosition().set(1, 3, 1);

        entityController = new EntityCameraController(player);
        window.addCursorCallback(entityController::handleCursorMove);

        eventBus.post(new GameReadyEvent(context));

        // TODO:
        Random random = new Random();
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                for (int top = random.nextInt(3) + 3, y = top; y >= 0; y--) {
                    world.setBlock(BlockPos.of(x, y, z), y == top ? Blocks.GRASS : Blocks.DIRT);
                }
            }
        }
    }

    @Override
    public void run() {
        super.run();
        ticker.start(); // start to tick
    }

    private void clientTick() {
        keyBindingManager.tick();
        // TODO upload particle physics here
    }

    /**
     * Actual render call
     *
     * @param partialTick
     */
    private void renderTick(double partialTick) {
        window.beginDraw();
        this.renderContext.render(partialTick);
        window.endDraw();
    }

    // https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/GameEngine.java
    private void gameLoop() {
        long lastTime;
        while (!stopped) {
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
    /**
     * Replaced by registerKeyBindings
     * 
     * @return
     */
    @Deprecated
    List<Action> buildActions() {
        return Collections.emptyList();
//        return Lists.newArrayList(ActionBuilderImpl.create("player.move.forward").setStartHandler().setEndHandler().build(),
//                ActionBuilderImpl.create("player.move.backward").setStartHandler().setEndHandler().build(), ActionBuilderImpl.create("player.move.left").setStartHandler().setEndHandler().build(),
//                ActionBuilderImpl.create("player.move.right").setStartHandler().setEndHandler().build(), ActionBuilderImpl.create("player.move.jump").setStartHandler().setEndHandler().build(),
//                ActionBuilderImpl.create("player.move.sneak").setStartHandler().setEndHandler().build(), ActionBuilderImpl.create("player.mouse.left").setStartHandler().build(),
//                ActionBuilderImpl.create("player.mouse.right").setStartHandler().build());
    }

    /**
     * For now. Registers the key bindings
     * 
     * @param manager The KeyBindingManager to register the bindings
     */
    void registerKeyBindings(KeyBindingManager manager) {
        manager.register(KeyBinding.create("player.move.forward", Key.KEY_W, (c) -> getEntityController().handleMotion(MotionType.FORWARD, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.FORWARD, false)));
        manager.register(KeyBinding.create("player.move.backward", Key.KEY_S, (c) -> getEntityController().handleMotion(MotionType.BACKWARD, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.BACKWARD, false)));
        manager.register(KeyBinding.create("player.move.left", Key.KEY_A, (c) -> getEntityController().handleMotion(MotionType.LEFT, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.LEFT, false)));
        manager.register(KeyBinding.create("player.move.right", Key.KEY_D, (c) -> getEntityController().handleMotion(MotionType.RIGHT, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.RIGHT, false)));
        manager.register(KeyBinding.create("player.move.jump", Key.KEY_SPACE, (c) -> getEntityController().handleMotion(MotionType.UP, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.UP, false)));
        manager.register(KeyBinding.create("player.move.sneak", Key.KEY_LEFT_SHIFT, (c) -> getEntityController().handleMotion(MotionType.DOWN, true), ActionMode.PRESS)
                .endAction((c, i) -> getEntityController().handleMotion(MotionType.DOWN, false)));
        manager.register(KeyBinding.create("player.mouse.left", Key.MOUSE_BUTTON_LEFT, (c) -> {
            BlockPrototype.Hit hit = getRenderContext().getHit();
            if (hit != null) {
                getWorld().setBlock(hit.getPos(), c.getBlockAir());
            }
        }, ActionMode.PRESS));
        manager.register(KeyBinding.create("player.mouse.right", Key.MOUSE_BUTTON_RIGHT, (c) -> {
            BlockPrototype.Hit hit = getRenderContext().getHit();
            if (hit != null) {
                getWorld().setBlock(hit.getFace().offset(hit.getPos()), Blocks.DIRT);
            }
        }, ActionMode.PRESS));
    }

//    private List<Action> buildActions() {
//        List<Action> list = motionController.getActions();
//        list.addAll(Lists.newArrayList(
//                ActionBuilderImpl.create("player.mouse.right").setStartHandler((c) -> {
//                    Player player = this.player;
//                    Camera camera = this.camera;
//
//                    if (player == null) return;
//                    Entity mountingEntity = player.getControlledEntity();
//                    World world = player.getWorld();
//
//                    if (mountingEntity == null || world == null) return;
//
//                    TwoHands twoHands = mountingEntity.getBehavior(TwoHands.class);
//                    if (twoHands == null) return;
//
//                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);
//
//                    Item hand = twoHands.getMainHand();
//                    if (hand != null) {
//                        if (hit != null) {
//                            hand.onUseBlockStart(world, mountingEntity, hand, hit);
//                        } else {
//                            hand.onUseStart(world, mountingEntity, hand);
//                        }
//                    }
//                    if (hit != null) {
//                        if (hit.block.shouldActivated(world, mountingEntity, hit.position, hit.block)) {
//                            hit.block.onActivated(world, mountingEntity, hit.position, hit.block);
//                        }
//                    }
//                }).build(),
//                ActionBuilderImpl.create("player.mouse.left").setStartHandler((c) -> {
//                    Player player = this.player;
//                    Camera camera = this.camera;
//
//                    if (player == null) return;
//                    Entity mountingEntity = player.getControlledEntity();
//                    World world = player.getWorld();
//
//                    if (mountingEntity == null || world == null) return;
//
//                    TwoHands twoHands = mountingEntity.getBehavior(TwoHands.class);
//                    if (twoHands == null) return;
//
//                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);
//                    if (twoHands.getMainHand() != null) {
//                        if (hit != null) {
//                            world.setBlock(hit.position, null);
////                            mainHand.onUseBlockStart(world, this, mainHand, hit);
//                        } else {
////                            mainHand.onUseStart(world, this, mainHand);
//                        }
//                    }
//                    if (hit != null) {
//                        world.setBlock(hit.position, null);
////                        if (hit.block.shouldActivated(world, this, hit.position, hit.block)) {
////                            hit.block.onActivated(world, this, hit.position, hit.block);
////                        }
//                    }
//                }).build()
//        ));
//        return list;
//    }
//
//    @Deprecated
//    class MotionController {
//        static final int RUNNING = 0, SNEAKING = 1,
//                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
//                JUMPING = 6;
//        float factor = 0.1f;
//        private Vector3f movingDirection = new Vector3f();
//        private boolean[] phases = new boolean[16];
//
//        List<Action> getActions() {
//            return Lists.newArrayList(
//                    ActionBuilderImpl.create("player.move.forward").setStartHandler((c) -> accept(FORWARD, true))
//                            .setEndHandler((c, i) -> accept(FORWARD, false)).build(),
//                    ActionBuilderImpl.create("player.move.backward").setStartHandler((c) -> accept(BACKWARD, true))
//                            .setEndHandler((c, i) -> accept(BACKWARD, false)).build(),
//                    ActionBuilderImpl.create("player.move.left").setStartHandler((c) -> accept(LEFT, true))
//                            .setEndHandler((c, i) -> accept(LEFT, false)).build(),
//                    ActionBuilderImpl.create("player.move.right").setStartHandler((c) -> accept(RIGHT, true))
//                            .setEndHandler((c, i) -> accept(RIGHT, false)).build(),
//                    ActionBuilderImpl.create("player.move.jump").setStartHandler((c) -> accept(JUMPING, true))
//                            .setEndHandler((c, i) -> accept(JUMPING, false)).build(),
//                    ActionBuilderImpl.create("player.move.sneak").setStartHandler((c) -> accept(SNEAKING, true))
//                            .setEndHandler((c, i) -> accept(SNEAKING, false)).build()
//            );
//        }
//
//        private void accept(int action, boolean phase) {
//            if (phases[action] == phase) return;
//            this.phases[action] = phase;
//
//            Vector3f movingDirection = this.movingDirection;
//            switch (action) {
//                case FORWARD:
//                case BACKWARD:
//                    if (this.phases[FORWARD] == this.phases[BACKWARD]) {
//                        movingDirection.z = 0;
//                    }
//                    if (this.phases[FORWARD]) movingDirection.z = -factor;
//                    else if (this.phases[BACKWARD]) movingDirection.z = +factor;
//                    break;
//                case LEFT:
//                case RIGHT:
//                    if (this.phases[LEFT] == this.phases[RIGHT]) {
//                        movingDirection.x = 0;
//                    }
//                    if (this.phases[RIGHT]) movingDirection.x = factor;
//                    else if (this.phases[LEFT]) movingDirection.x = -factor;
//                    break;
//                case RUNNING:
//                    break;
//                case JUMPING:
//                case SNEAKING:
//                    if (this.phases[JUMPING] && !this.phases[SNEAKING]) {
//                        movingDirection.y = factor;
//                    } else if (this.phases[SNEAKING] && !this.phases[JUMPING]) {
//                        movingDirection.y = -factor;
//                    } else {
//                        movingDirection.y = 0;
//                    }
//                    break;
//            }
//            Vector3f f = camera.getFrontVector();
//            f.y = 0;
//            movingDirection.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), f),
//                    player.getControlledEntity().getMotion());
//        }
//    }
}