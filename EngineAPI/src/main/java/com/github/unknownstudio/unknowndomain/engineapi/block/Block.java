package com.github.unknownstudio.unknowndomain.engineapi.block;

import com.github.unknownstudio.unknowndomain.engineapi.math.BlockPos;
import com.github.unknownstudio.unknowndomain.engineapi.math.BoundingBox;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public interface Block extends RegistryEntry<Block> {

	BoundingBox getBoundingBox();
	
    boolean onBlockPlaced(BlockPos pos);
    
    boolean onBlockDestroyed(BlockPos pos, boolean harvested);

}
