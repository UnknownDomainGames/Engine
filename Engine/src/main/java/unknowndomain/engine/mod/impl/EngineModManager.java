package unknowndomain.engine.mod.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.*;
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
    public ModDescriptorFinder createModDescriptorFinder() {
        return new JsonModDescriptorFinder();
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

        ModDescriptor modDescriptor = modDescriptorFinder.find(modPath);

        if (isModLoaded(modDescriptor.getModId())) {
            throw new ModAlreadyLoadedException(modDescriptor.getModId());
        }

        DependencyManager.CheckResult result = dependencyManager.checkDependencies(modDescriptor.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(modDescriptor.getModId(), result);
        }

        Logger modLogger = LoggerFactory.getLogger(modDescriptor.getModId());

        ModClassLoader classLoader = new ModClassLoader(modLogger, Thread.currentThread().getContextClassLoader());
        for (Path directory : directories) {
            classLoader.addPath(directory);
        }

        DevModContainer modContainer;
        try {
            Object instance = Class.forName(modDescriptor.getMainClass(), true, classLoader).newInstance();
            ModAssets assets = new DevModAssets(directories);
            modContainer = new DevModContainer(modDescriptor, classLoader, assets, modLogger, instance);
            classLoader.setMod(modContainer);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ModLoadException(modDescriptor.getModId(), e);
        }

        loadedModContainer.put(modContainer.getModId(), modContainer);
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
