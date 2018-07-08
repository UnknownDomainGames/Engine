package unknowndomain.engine.api.registry;

import java.util.List;

import unknowndomain.engine.api.resource.ResourceLocation;

/**
 * Engine-level registry.
 *
 * IResourcesManager is used to handle any resources loaded in loading stage. E.g. blocks, items, textures, audio
 *
 * Modders should not use this interface
 * @param <T>
 */
public interface ResourcesManager<T extends RegistryEntry<T>> extends Registry<T> {

    List<T> getAllEntries();

    T getEntry(ResourceLocation location);

    default T getEntry(String name){
        return getEntry(new ResourceLocation(name));
    }
}
