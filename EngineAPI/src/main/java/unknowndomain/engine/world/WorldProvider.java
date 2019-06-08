package unknowndomain.engine.world;

import unknowndomain.engine.registry.RegistryEntry;

import java.nio.file.Path;

public interface WorldProvider<T extends World> extends RegistryEntry<WorldProvider<T>> {

    T create(String name, Path storagePath);
}
