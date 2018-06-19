package com.github.unknownstudio.unknowndomain.engineapi.registry;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.util.List;

/**
 * Engine-level registry.
 *
 * IResourcesManager is used to handle any resources loaded in loading stage. E.g. blocks, items, textures, audio
 *
 * Modders should not use this interface
 * @param <T>
 */
public interface IResourcesManager<T extends RegistryEntry<T>> extends Registry<T> {

    List<T> getAllEntries();

    T getEntry(ResourceLocation location);

    default T getEntry(String name){
        return getEntry(new ResourceLocation(name));
    }
}
