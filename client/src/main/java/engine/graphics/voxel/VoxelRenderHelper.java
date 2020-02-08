package engine.graphics.voxel;

import engine.graphics.RenderManager;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.block.BlockRenderManagerImpl;
import engine.graphics.item.ItemRenderManager;
import engine.graphics.item.ItemRenderManagerImpl;

public class VoxelRenderHelper {

    private static BlockRenderManagerImpl blockRenderManager;
    private static ItemRenderManagerImpl itemRenderManager;

    public static void initialize(RenderManager context) {
        blockRenderManager = new BlockRenderManagerImpl();
        blockRenderManager.init();
        BlockRenderManager.Internal.setInstance(blockRenderManager);

        itemRenderManager = new ItemRenderManagerImpl();
        itemRenderManager.init(context);
        ItemRenderManager.Internal.setInstance(itemRenderManager);
    }
}
