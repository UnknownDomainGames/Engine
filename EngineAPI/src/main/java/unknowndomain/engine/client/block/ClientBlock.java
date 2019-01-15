package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.block.BlockRenderType;
import unknowndomain.engine.client.rendering.block.BlockRenderer;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

public interface ClientBlock extends RegistryEntry<ClientBlock> {

    Block getBlock();

    default boolean isVisible() {
        return getRenderer() != null;
    }

    boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing);

    boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing);

    BlockRenderer getRenderer();

    BlockRenderType getRenderType();
}
