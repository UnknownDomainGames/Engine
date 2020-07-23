package engine.graphics.voxel;

import engine.client.EngineClient;
import engine.client.asset.AssetManager;
import engine.client.asset.AssetType;
import engine.client.asset.reloading.AssetReloadHandler;
import engine.event.Listener;
import engine.event.Order;
import engine.event.engine.EngineEvent;
import engine.event.game.GameTerminationEvent;
import engine.event.player.PlayerControlEntityEvent;
import engine.graphics.GraphicsManager;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.item.ItemRenderManager;
import engine.graphics.item.ItemRenderManagerImpl;
import engine.graphics.model.BakedModel;
import engine.graphics.model.voxel.ModelManager;
import engine.graphics.texture.TextureAtlas;
import engine.graphics.texture.TextureAtlasImpl;
import engine.graphics.voxel.chunk.ChunkRenderer;
import engine.world.World;

public final class VoxelGraphicsHelper {

    private static BlockRenderManagerImpl blockRenderManager;
    private static ItemRenderManagerImpl itemRenderManager;
    private static TextureAtlasImpl textureAtlas;

    private static ChunkRenderer renderer;

    public static void initialize(GraphicsManager manager) {
        textureAtlas = new TextureAtlasImpl();
        VoxelGraphics.setVoxelTextureAtlas(textureAtlas);
        AssetManager assetManager = manager.getEngine().getAssetManager();
        assetManager.getReloadManager().addHandler(
                AssetReloadHandler.builder().name("VoxelTexture").after("Texture").runnable(textureAtlas::reload).build());
        assetManager.getReloadManager().addHandler(
                AssetReloadHandler.builder().name("VoxelTextureCleanCache").after("VoxelTexture").runnable(textureAtlas::cleanCache).build());

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

        manager.getEngine().getEventBus().register(VoxelGraphicsHelper.class);
    }

    public static TextureAtlas getVoxelTextureAtlas() {
        return textureAtlas;
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        if (event.getEngine() instanceof EngineClient) { //TODO: remove this if integrated server no longer use "Engine" type
            blockRenderManager.init();
            itemRenderManager.init();
            AssetManager.instance().reload();
        }
    }

    @Listener(order = Order.LAST)
    public static void onControlEntity(PlayerControlEntityEvent.Post event) {
        World world = event.getNewEntity().getWorld();
        if (renderer != null && renderer.isEqualsWorld(world)) return;
        renderer = new ChunkRenderer(GraphicsManager.instance(), world);
    }

    @Listener
    public static void onGameMarkedTermination(GameTerminationEvent.Marked event) {
        if (renderer != null) {
            renderer.dispose();
            renderer = null;
        }
    }
}
