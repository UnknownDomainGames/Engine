package unknowndomain.engine.world;

import unknowndomain.engine.registry.RegistryEntry;

public abstract class BaseWorldProvider<T extends World> extends RegistryEntry.Impl<WorldProvider<T>> implements WorldProvider<T> {
}
