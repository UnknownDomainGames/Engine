package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.exception.MissingDependencyException;
import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;

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

    @Nonnull
    @Override
    public ModContainer loadMod(ModDescriptor modDescriptor) {
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
        } catch (Exception e) {
            throw new ModLoadException(modDescriptor.getModId(), e);
        }

        if (modContainer == null) {
            throw new ModLoadException(modDescriptor.getModId());
        }

        loadedModContainer.put(modContainer.getModId(), modContainer);
        return modContainer;
    }

    @Nonnull
    @Override
    public Collection<ModContainer> loadMod(Iterator<Path> pathIterator) throws ModLoadException, ModAlreadyLoadedException {
        List<ModContainer> modContainers = new ArrayList<>();
        while (pathIterator.hasNext()) {
            modContainers.add(loadMod(pathIterator.next()));
        }
        return modContainers;
    }

    @Nullable
    @Override
    public ModContainer getMod(String modId) {
        return loadedModContainer.get(modId);
    }

    @Nullable
    @Override
    public ModContainer getMod(Class<?> clazz) {
        throw new UnsupportedOperationException(); // TODO:
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
