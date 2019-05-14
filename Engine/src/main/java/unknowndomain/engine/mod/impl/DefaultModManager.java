package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.DependencyManager;
import unknowndomain.engine.mod.ModDescriptorFinder;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.java.JavaModLoader;

public class DefaultModManager extends AbstractModManager {

    @Override
    public ModLoader createModLoader() {
        return new JavaModLoader();
    }

    @Override
    public ModDescriptorFinder createModDescriptorFinder() {
        return new JsonModDescriptorFinder();
    }

    @Override
    public DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }
}
