package unknowndomain.engine.mod.impl;

import org.apache.commons.lang3.SystemUtils;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.DependencyManager;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptorFinder;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.mod.java.ModClassLoader;
import unknowndomain.engine.mod.misc.DefaultModDescriptor;
import unknowndomain.engine.util.RuntimeEnvironment;

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

    public void loadDevEnvMod() {
        if (Platform.getEngine().getRuntimeEnvironment() != RuntimeEnvironment.MOD_DEVELOPMENT)
            return;

        List<Path> directories = findDirectoriesInClassPath();

        Path modPath = findModInDirectories(directories);
        if (modPath == null)
            return;

        ModContainer modContainer = loadMod(modPath);
        ModClassLoader classLoader = (ModClassLoader) modContainer.getClassLoader();
        for (Path directory : directories) {
            classLoader.addPath(directory);
        }
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
