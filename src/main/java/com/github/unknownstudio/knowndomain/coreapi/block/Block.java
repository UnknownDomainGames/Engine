package com.github.unknownstudio.knowndomain.coreapi.block;

import com.github.unknownstudio.knowndomain.coreapi.maths.BoundingBox;
import com.github.unknownstudio.knowndomain.coreapi.registry.RegistryEntry;

public class Block implements RegistryEntry<Block> {

    private int hardness;

    private int antiExplosive;

    private int state;

    private BoundingBox boundingBox;

    private String registryName;

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public void setRegistryName(String name) {
        registryName = name;
    }
}
