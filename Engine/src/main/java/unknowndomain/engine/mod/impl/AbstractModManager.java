package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.dummy.DummyModContainer;
import unknowndomain.engine.mod.exception.MissingDependencyException;
import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;
import unknowndomain.engine.mod.java.ModClassLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractModManager implements ModManager {

    protected final Map<String, ModContainer> loadedModContainer = new HashMap<>();

    protected final ModLoader modLoader = createModLoader();
    protected final ModDescriptorFinder modDescriptorFinder = createModDescriptorFinder();
    protected final DependencyManager dependencyManager = createDependencyManager();

    protected abstract ModLoader createModLoader();

    protected abstract ModDescriptorFinder createModDescriptorFinder();

    protected abstract DependencyManager createDependencyManager();

    @Nonnull
    @Override
    public ModContainer loadMod(Path path) {
        return loadMod(modDescriptorFinder.find(path));
    }

    protected ModContainer loadMod(ModDescriptor modDescriptor) {
        if (isModLoaded(modDescriptor.getModId())) {
            throw new ModAlreadyLoadedException(modDescriptor.getModId());
        }

        DependencyManager.CheckResult result = dependencyManager.checkDependencies(modDescriptor.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(modDescriptor.getModId(), result);
        }

        ModContainer modContainer;
        try {
            modContainer = modLoader.load(modDescriptor);
        } catch (ModLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModLoadException(modDescriptor.getModId(), e);
        }

        if (modContainer == null) {
            throw new ModLoadException(modDescriptor.getModId());
        }

        loadedModContainer.put(modContainer.getModId(), modContainer);
        return modContainer;
    }

    protected void addDummyModContainer(ModDescriptor modDescriptor) {
        loadedModContainer.put(modDescriptor.getModId(), new DummyModContainer(modDescriptor));
    }

    @Nullable
    @Override
    public Optional<ModContainer> getMod(String modId) {
        return Optional.ofNullable(loadedModContainer.get(modId));
    }

    @Nullable
    @Override
    public Optional<ModContainer> getMod(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        return classLoader instanceof ModClassLoader ? Optional.of(((ModClassLoader) classLoader).getMod()) : Optional.empty();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return loadedModContainer.containsKey(modId);
    }

    @Nonnull
    @Override
    public Collection<ModContainer> getLoadedMods() {
        return loadedModContainer.values();
    }
}
