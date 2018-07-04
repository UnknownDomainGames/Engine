package com.github.unknownstudio.unknowndomain.engineapi.world;

import com.github.unknownstudio.unknowndomain.engineapi.block.Block;
import com.github.unknownstudio.unknowndomain.engineapi.math.BlockPos;

public interface World {
	
	String getName();
	
	Block getBlock(int x, int y, int z);
	
	Block getBlock(BlockPos pos);

}
