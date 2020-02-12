package engine.graphics.voxel;

import engine.client.asset.AssetManager;
import engine.client.asset.AssetType;
import engine.client.asset.reloading.AssetReloadListener;
import engine.event.Listener;
import engine.event.Order;
import engine.event.engine.EngineEvent;
import engine.event.game.GameTerminationEvent;
import engine.event.player.PlayerControlEntityEvent;
import engine.graphics.RenderManager;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.item.ItemRenderManager;
import engine.graphics.item.ItemRenderManagerImpl;
import engine.graphics.model.BakedModel;
import engine.graphics.model.voxel.ModelManager;
import engine.graphics.texture.TextureAtlas;
import engine.graphics.texture.TextureAtlasImpl;
import engine.graphics.voxel.chunk.ChunkBaker;
import engine.graphics.voxel.chunk.ChunkRenderer;
import engine.world.World;

public final class VoxelRenderHelper {

    private static BlockRenderManagerImpl blockRenderManager;
    private static ItemRenderManagerImpl itemRenderManager;
    private static TextureAtlasImpl textureAtlas;

    public static void initialize(RenderManager manager) {
        textureAtlas = new TextureAtlasImpl();
        AssetManager assetManager = manager.getEngine().getAssetManager();
        assetManager.getReloadManager().addListener(
                AssetReloadListener.builder().name("VoxelTexture").after("Texture").runnable(textureAtlas::reload).build());
        assetManager.getReloadManager().addListener(
                AssetReloadListener.builder().name("VoxelTextureCleanCache").after("VoxelTexture").runnable(textureAtlas::cleanCache).build());

        blockRenderManager = new BlockRenderManagerImpl();
        BlockRenderManager.Internal.setInstance(blockRenderManager);

        itemRenderManager = new ItemRenderManagerImpl();
        ItemRenderManager.Internal.setInstance(itemRenderManager);

        assetManager.register(AssetType
                .builder(BakedModel.class)
                .name("VoxelModel")
                .provider(new ModelManager())
                .parentLocation("model")
                .extensionName(".json")
                .build());

        ChunkBaker.start();

        manager.getEngine().getEventBus().register(VoxelRenderHelper.class);
    }

    public static TextureAtlas getVoxelTextureAtlas() {
        return textureAtlas;
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        blockRenderManager.init();
        itemRenderManager.init();
    }

    @Listener
    public static void onGameMarkedTermination(GameTerminationEvent.Marked event) {
        ChunkBaker.stop();
    }

    private static ChunkRenderer renderer;

    @Listener(order = Order.LAST)
    public static void onControlEntity(PlayerControlEntityEvent.Post event) {
        World world = event.getNewEntity().getWorld();
        renderer = new ChunkRenderer(RenderManager.instance(), world);
        AssetManager.instance().reload();
    }
}
