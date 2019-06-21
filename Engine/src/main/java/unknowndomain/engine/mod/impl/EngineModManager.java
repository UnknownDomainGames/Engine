package unknowndomain.engine.mod.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.DependencyManager;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModMetadataFinder;
import unknowndomain.engine.mod.exception.MissingDependencyException;
import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.mod.java.ModClassLoader;
import unknowndomain.engine.mod.java.dev.DevModAssets;
import unknowndomain.engine.mod.java.dev.DevModContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static unknowndomain.engine.util.ClassPathUtils.getDirectoriesInClassPath;

public class EngineModManager extends AbstractModManager {

    @Override
    public ModLoader createModLoader() {
        return new JavaModLoader();
    }

    @Override
    public ModMetadataFinder createModDescriptorFinder() {
        return new JsonModMetadataFinder();
    }

    @Override
    public DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    public void loadDevEnvMod() {
        List<Path> directories = getDirectoriesInClassPath();

        Path modPath = findModInDirectories(directories);
        if (modPath == null)
            return;

        ModMetadata modMetadata = modMetadataFinder.find(modPath);

        if (isModLoaded(modMetadata.getId())) {
            throw new ModAlreadyLoadedException(modMetadata.getId());
        }

        DependencyManager.CheckResult result = dependencyManager.checkDependencies(modMetadata.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(modMetadata.getId(), result);
        }

        Logger modLogger = LoggerFactory.getLogger(modMetadata.getId());

        ModClassLoader classLoader = new ModClassLoader(modLogger, Thread.currentThread().getContextClassLoader());
        for (Path directory : directories) {
            classLoader.addPath(directory);
        }

        DevModContainer modContainer;
        try {
            Object instance = Class.forName(modMetadata.getMainClass(), true, classLoader).newInstance();
            DevModAssets assets = new DevModAssets(directories);
            modContainer = new DevModContainer(modMetadata, classLoader, assets, modLogger, instance);
            classLoader.setMod(modContainer);
            assets.setMod(modContainer);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ModLoadException(modMetadata.getId(), e);
        }

        loadedModContainer.put(modContainer.getId(), modContainer);
    }

    private Path findModInDirectories(List<Path> paths) {
        for (Path path : paths) {
            if (Files.exists(path.resolve("metadata.json"))) {
                return path;
            }
        }
        return null;
    }
}
