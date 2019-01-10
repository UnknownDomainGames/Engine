package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.block.BlockRenderer;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

// TODO: Add block renderer
public interface ClientBlock extends RegistryEntry<ClientBlock> {

    Block getBlock();

    boolean isRenderable();

    boolean canRenderFace(BlockAccessor world, BlockPos pos, Facing facing);

    boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing);

    BlockRenderer getRenderer();
}
