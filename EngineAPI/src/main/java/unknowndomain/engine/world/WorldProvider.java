package unknowndomain.engine.world;

import unknowndomain.engine.registry.RegistryEntry;

import java.nio.file.Path;

public interface WorldProvider<T extends World> extends RegistryEntry<WorldProvider<T>> {

    /**
     *
     * @param name Name of the world created
     * @param storagePath Storage path of ALL worlds
     * @return
     */
    T create(String name, Path storagePath);
}
