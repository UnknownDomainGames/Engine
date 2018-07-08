package unknowndomain.engine.api.world;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;

public interface World {
	
	String getName();
	
	Block getBlock(int x, int y, int z);
	
	Block getBlock(BlockPos pos);

}
