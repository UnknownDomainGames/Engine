package unknowndomain.engine.api.block;

import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.registry.RegistryEntry;

public interface Block extends RegistryEntry<Block> {

	BoundingBox getBoundingBox();
	
    boolean onBlockPlaced(BlockPos pos);
    
    boolean onBlockDestroyed(BlockPos pos, boolean harvested);

}
