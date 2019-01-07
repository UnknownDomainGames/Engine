package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

public interface ClientBlock extends RegistryEntry<ClientBlock> {

    Block getBlock();

    boolean canRenderFace(BlockAccessor world, BlockPos.Mutable pos, Facing facing);

    boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos.Mutable pos, Facing facing);
}
