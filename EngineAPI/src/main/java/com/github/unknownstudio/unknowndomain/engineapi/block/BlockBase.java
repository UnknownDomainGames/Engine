package com.github.unknownstudio.unknowndomain.engineapi.block;

import com.github.unknownstudio.unknowndomain.engineapi.math.BlockPos;
import com.github.unknownstudio.unknowndomain.engineapi.math.BoundingBox;

public class BlockBase implements Block {

    private int hardness;

    private int antiExplosive;

    protected BoundingBox boundingBox;

    private String registryName;

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public void setRegistryName(String name) {
        registryName = name;
    }

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
