package nullengine.world;

import nullengine.registry.RegistryEntry;

public abstract class BaseWorldProvider<T extends World> extends RegistryEntry.Impl<WorldProvider<T>> implements WorldProvider<T> {
}
