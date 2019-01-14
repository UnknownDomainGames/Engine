package unknowndomain.engine.client.game;

import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.input.controller.EntityCameraController;
import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.client.rendering.camera.FirstPersonCamera;
import unknowndomain.engine.client.rendering.texture.TextureTypes;
import unknowndomain.engine.event.game.GameReadyEvent;
import unknowndomain.engine.game.GameServerFullAsync;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.FixStepTicker;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.player.PlayerImpl;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;
import unknowndomain.game.Blocks;

import java.util.Random;
import java.util.UUID;

public class GameClientStandalone extends GameServerFullAsync {

    private ClientContextImpl clientContext;
    private EntityController entityController;

    private WorldCommon world;
    private Player player;

    private FixStepTicker.Dynamic ticker;

    private boolean stopped;

    public GameClientStandalone(EngineClient engine, Option option) {
        super(engine, option);

        this.ticker = new FixStepTicker.Dynamic(this::clientTick, this::renderTick, FixStepTicker.clientTick);
//Removed
//        // Remove it
//        bus.register(new DefaultGameMode());
    }

    public EngineClient engine() {
        return (EngineClient) engine;
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

    public EntityController getEntityController() {
        return entityController;
    }

    public ClientContextImpl getClientContext() {
        return clientContext;
    }

    @Override
    protected void constructStage() {
        super.constructStage();
        // Moved to Engine
        // resourceManager = new ResourceManagerImpl();
        // resourceManager.addResourceSource(new ResourceSourceBuiltin());

        player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
    }

    @Override
    protected void registerStage() {
        super.registerStage();

        // actionManager = new ActionManagerImpl(context,
        // this.context.getRegistryManager().getRegistryManager(Action.class));
        // actionManager.registerAll(buildActions().toArray(new Action[0]));

        // registerKeyBindings(keyBindingManager);

        // Moved to Engine

        // Moved to EngineDummyContainer
//        List<Renderer.Factory> factories = Lists.newArrayList();
//        ClientRegistryEvent clientRegistryEvent = new ClientRegistryEvent(factories);
//        eventBus.post(clientRegistryEvent);
//
//        //  Don't initialize renderer at here. It should be initialized in Engine
//        factories.add((context, manager) -> {
//            Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
//            byte[] cache = resource.cache();
//            return new GuiRenderer(ByteBuffer.allocateDirect(cache.length).put(cache).flip(),
//                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(), ShaderType.VERTEX_SHADER),
//                    Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(), ShaderType.FRAGMENT_SHADER));
//        });

        clientContext = new ClientContextImpl(this, Thread.currentThread(), engine().getRendererFactories(), engine().getWindow(), player);
        clientContext.build(context, engine().getResourceManager());
        clientContext.setCamera(new FirstPersonCamera(player));
    }

    @Override
    protected void resourceStage() {
        // Moved to Engine
        // eventBus.post(new ResourceSetupEvent(context, resourceManager,
        // clientContext.getTextureManager()));
        clientContext.getTextureManager().initTextureAtlas(TextureTypes.BLOCK);
    }

    @Override
    protected void finishStage() {
        spawnWorld(null);

        // TODO:
        world = (WorldCommon) getWorld("default");
        world.playerJoin(player);
        player.getControlledEntity().getPosition().set(1, 3, 1);

        entityController = new EntityCameraController(player);
        engine().getWindow().addCursorCallback(entityController::handleCursorMove);
        // For now until we figure out how to setup games
        engine().getKeyBindingManager().setGameContext(clientContext);

        engine.getEventBus().post(new GameReadyEvent(context));

        // TODO:
        Random random = new Random();
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                for (int top = random.nextInt(3) + 3, y = top; y >= 0; y--) {
                    world.setBlock(BlockPos.of(x, y, z), y == top ? Blocks.GRASS : Blocks.DIRT, null);
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
        engine().getKeyBindingManager().tick();
        // TODO upload particle physics here
    }

    /**
     * Actual render call
     *
     * @param partialTick
     */
    private void renderTick(double partialTick) {
        engine().getWindow().beginDraw();
        clientContext.updateFps();
        this.clientContext.render(partialTick);
        engine().getWindow().endDraw();
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