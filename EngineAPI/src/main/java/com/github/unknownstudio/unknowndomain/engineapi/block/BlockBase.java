package com.github.unknownstudio.unknowndomain.engineapi.block;

import com.github.unknownstudio.unknowndomain.engineapi.math.BlockPos;
import com.github.unknownstudio.unknowndomain.engineapi.math.BoundingBox;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class BlockBase extends RegistryEntry.Impl<Block> implements Block {

    private int hardness;

    private int antiExplosive;

    protected BoundingBox boundingBox;

    @Override
    public boolean onBlockPlaced(BlockPos pos){
        return true;
    }

    @Override
    public boolean onBlockDestroyed(BlockPos pos, boolean harvested){
        return true;
    }

    @Override
    public BoundingBox getBoundingBox(){
        return boundingBox;
    }
}
