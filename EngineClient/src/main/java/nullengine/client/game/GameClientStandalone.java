package nullengine.client.game;

import nullengine.block.Block;
import nullengine.client.EngineClient;
import nullengine.client.gui.Scene;
import nullengine.client.input.controller.EntityCameraController;
import nullengine.client.input.controller.EntityController;
import nullengine.client.rendering.camera.FirstPersonCamera;
import nullengine.entity.item.ItemEntity;
import nullengine.event.game.GameTerminationEvent;
import nullengine.game.GameServerFullAsync;
import nullengine.item.ItemStack;
import nullengine.player.Player;
import nullengine.world.World;
import nullengine.world.WorldCommon;
import nullengine.world.WorldCommonProvider;
import nullengine.world.gen.ChunkGeneratorFlat;
import org.joml.Vector3d;
import unknowndomaingame.foundation.client.gui.hud.HUDGame;
import unknowndomaingame.foundation.init.Blocks;
import unknowndomaingame.foundation.init.Items;

import javax.annotation.Nonnull;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;
    private final Player player;

    private EntityController entityController;

    public GameClientStandalone(EngineClient engine, Player player) {
        super(engine);
        this.engineClient = engine;
        this.player = player;
    }

    @Nonnull
    @Override
    public EngineClient getEngine() {
        return engineClient;
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

    @Override
    protected void constructStage() {
        super.constructStage();
    }

    @Override
    protected void resourceStage() {
        super.resourceStage();

        engineClient.getAssetManager().reload();
    }

    @Override
    protected void finishStage() {
        logger.info("Finishing Game Initialization!");

        // TODO: Remove it
        WorldCommonProvider provider = new WorldCommonProvider();
        provider.setChunkGenerator(new ChunkGeneratorFlat(new ChunkGeneratorFlat.Setting().setLayers(new Block[]{Blocks.DIRT, Blocks.DIRT, Blocks.DIRT, Blocks.DIRT, Blocks.GRASS})));
        spawnWorld(provider, "default");
        var world = (WorldCommon) getWorld("default");
        world.onPlayerJoin(player);
        player.getControlledEntity().getPosition().set(0, 5, 0);

        engineClient.getRenderContext().setCamera(new FirstPersonCamera(player));

        entityController = new EntityCameraController(player);
        engineClient.getRenderContext().getWindow().addCursorCallback((window, xpos, ypos) -> {
            entityController.handleCursorMove(xpos, ypos);
        });

        super.finishStage();
        logger.info("Game Ready!");

        // TODO: Remove it
//        Random random = new Random();
//        for (int x = -16; x < 16; x++) {
//            for (int z = -16; z < 16; z++) {
//                for (int top = 3, y = top; y >= 0; y--) {
//                    world.setBlock(BlockPos.of(x, y, z), y == top ? Blocks.GRASS : Blocks.DIRT, null);
//                }
//            }
//        }

        world.spawnEntity(new ItemEntity(world.getEntities().size(), world, new Vector3d(0, 5, 0), new ItemStack(Items.DIRT)));
        engineClient.getRenderContext().getGuiManager().showHud("game-hud", new Scene(new HUDGame()));
//        a = Platform.getEngineClient().getSoundManager().createSoundSource("test sound").position(25,5,0).gain(1.0f).speed(dir);
//        a.setLoop(true);
//        a.assignSound(sound);
//        a.play();
    }

    public void clientTick() {
        if (isMarkedTermination()) {
            tryTerminate();
            return;
        }
        // TODO upload particle physics here
    }

    @Override
    protected void tryTerminate() {
        logger.info("Game terminating!");
        engine.getEventBus().post(new GameTerminationEvent.Pre(this));
        super.tryTerminate();
        engine.getEventBus().post(new GameTerminationEvent.Post(this));
        logger.info("Game terminated.");
    }
}