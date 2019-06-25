package nullengine.client.block;

import nullengine.block.Block;
import nullengine.client.rendering.block.BlockMeshGenerator;
import nullengine.client.rendering.block.BlockRenderType;
import nullengine.math.BlockPos;
import nullengine.registry.RegistryEntry;
import nullengine.util.Facing;
import nullengine.world.BlockAccessor;

public interface ClientBlock extends RegistryEntry<ClientBlock> {

    Block getBlock();

    default boolean isVisible() {
        return getRenderer() != null;
    }

    boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing);

    boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing);

    BlockMeshGenerator getRenderer();

    BlockRenderType getRenderType();
}
