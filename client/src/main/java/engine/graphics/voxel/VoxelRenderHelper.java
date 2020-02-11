package engine.graphics.voxel;

import engine.client.asset.AssetManager;
import engine.client.asset.reloading.AssetReloadListener;
import engine.event.Listener;
import engine.event.engine.EngineEvent;
import engine.graphics.RenderManager;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.item.ItemRenderManager;
import engine.graphics.item.ItemRenderManagerImpl;
import engine.graphics.texture.TextureAtlas;
import engine.graphics.texture.TextureAtlasImpl;

public final class VoxelRenderHelper {

    private static BlockRenderManagerImpl blockRenderManager;
    private static ItemRenderManagerImpl itemRenderManager;
    private static TextureAtlasImpl textureAtlas;

    public static void initialize(RenderManager context) {
        textureAtlas = new TextureAtlasImpl();
        AssetManager assetManager = context.getEngine().getAssetManager();
        assetManager.getReloadManager().addListener(
                AssetReloadListener.builder().name("VoxelTexture").after("Texture").runnable(textureAtlas::reload).build());
        assetManager.getReloadManager().addListener(
                AssetReloadListener.builder().name("VoxelTextureCleanCache").after("VoxelTexture").runnable(textureAtlas::cleanCache).build());

        blockRenderManager = new BlockRenderManagerImpl();
        BlockRenderManager.Internal.setInstance(blockRenderManager);

        itemRenderManager = new ItemRenderManagerImpl();
        ItemRenderManager.Internal.setInstance(itemRenderManager);

        context.getEngine().getEventBus().register(VoxelRenderHelper.class);
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        blockRenderManager.init();
        itemRenderManager.init();
    }

    public static TextureAtlas getVoxelTextureAtlas() {
        return textureAtlas;
    }
}
