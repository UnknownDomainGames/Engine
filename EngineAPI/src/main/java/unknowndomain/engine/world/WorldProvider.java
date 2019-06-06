package unknowndomain.engine.world;

import unknowndomain.engine.registry.RegistryEntry;

import java.nio.file.Path;

public interface WorldProvider extends RegistryEntry<WorldProvider> {

    World create(String name, Path storagePath);
}
