package nullengine.client.rendering.voxel;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.block.BlockRenderManagerImpl;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.item.ItemRenderManagerImpl;

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
