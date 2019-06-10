package unknowndomain.engine.mod.impl;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.exception.MissingDependencyException;
import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.mod.java.ModClassLoader;
import unknowndomain.engine.mod.java.dev.DevModAssets;
import unknowndomain.engine.mod.java.dev.DevModContainer;
import unknowndomain.engine.mod.misc.DefaultModDescriptor;
import unknowndomain.engine.util.RuntimeEnvironment;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EngineModManager extends AbstractModManager {

    public EngineModManager() {
        addDummyModContainer(DefaultModDescriptor.builder().modId("engine").version(Platform.getVersion()).build());
    }

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

    public ModContainer loadDevEnvMod() {
        if (Platform.getEngine().getRuntimeEnvironment() != RuntimeEnvironment.MOD_DEVELOPMENT)
            return null;

        List<Path> directories = findDirectoriesInClassPath();

        Path modPath = findModInDirectories(directories);
        if (modPath == null)
            return null;

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
            ModAssets assets = new DevModAssets(FileSystems.getDefault(), directories);
            modContainer = new DevModContainer(modDescriptor, classLoader, assets, modLogger, instance);
            classLoader.setMod(modContainer);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ModLoadException(modDescriptor.getModId(), e);
        }

        loadedModContainer.put(modContainer.getModId(), modContainer);

        return modContainer;
    }

    private List<Path> findDirectoriesInClassPath() {
        List<Path> paths = new ArrayList<>();
        for (String path : SystemUtils.JAVA_CLASS_PATH.split(";")) {
            Path _path = Path.of(path);
            if (Files.isDirectory(_path)) {
                paths.add(_path);
            }
        }
        return paths;
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
