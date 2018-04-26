package com.github.unknownstudio.unknowndomain.engineapi.block;

import com.github.unknownstudio.unknowndomain.engineapi.math.BlockPos;
import com.github.unknownstudio.unknowndomain.engineapi.math.BoundingBox;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;

public class Block implements RegistryEntry<Block> {

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

    public boolean onBlockPlaced(BlockPos pos){
        return true;
    }

    public boolean onBlockDestroyed(BlockPos pos, boolean harvested){
        return true;
    }

    public BoundingBox getBoundingBox(){
        return boundingBox;
    }

}
