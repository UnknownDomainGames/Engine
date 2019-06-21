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
import java.util.*;

public abstract class AbstractModManager implements ModManager {

    protected final Map<String, ModContainer> loadedModContainer = new HashMap<>();

    protected final ModLoader modLoader = createModLoader();
    protected final ModMetadataFinder modMetadataFinder = createModDescriptorFinder();
    protected final DependencyManager dependencyManager = createDependencyManager();

    protected abstract ModLoader createModLoader();

    protected abstract ModMetadataFinder createModDescriptorFinder();

    protected abstract DependencyManager createDependencyManager();

    @Nonnull
    @Override
    public ModContainer loadMod(Path path) {
        List<Path> sources = List.of(path);
        return loadMod(sources, modMetadataFinder.find(sources));
    }

    protected ModContainer loadMod(Collection<Path> sources, ModMetadata modMetadata) {
        if (isModLoaded(modMetadata.getId())) {
            throw new ModAlreadyLoadedException(modMetadata.getId());
        }

        DependencyCheckResult result = dependencyManager.checkDependencies(modMetadata.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(modMetadata.getId(), result);
        }

        ModContainer modContainer;
        try {
            modContainer = modLoader.load(sources, modMetadata);
        } catch (ModLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModLoadException(modMetadata.getId(), e);
        }

        if (modContainer == null) {
            throw new ModLoadException(modMetadata.getId());
        }

        loadedModContainer.put(modContainer.getId(), modContainer);
        return modContainer;
    }

    public void addDummyModContainer(DummyModContainer dummyModContainer) {
        loadedModContainer.put(dummyModContainer.getId(), dummyModContainer);
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
