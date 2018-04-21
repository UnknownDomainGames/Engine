package com.github.unknownstudio.knowndomain.engineapi.block;

import com.github.unknownstudio.knowndomain.engineapi.math.BoundingBox;
import com.github.unknownstudio.knowndomain.engineapi.registry.RegistryEntry;

public class Block implements RegistryEntry<Block> {

    private int hardness;

    private int antiExplosive;

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
