package nullengine.mod.impl;

import nullengine.mod.DependencyManager;
import nullengine.mod.ModLoader;
import nullengine.mod.ModMetadata;
import nullengine.mod.ModMetadataFinder;
import nullengine.mod.exception.InvalidModMetadataException;
import nullengine.mod.java.JavaModLoader;
import nullengine.util.ClassPathUtils;

import java.nio.file.Path;
import java.util.List;

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
        List<Path> directories = ClassPathUtils.getDirectoriesInClassPath();
        try {
            ModMetadata metadata = modMetadataFinder.find(directories);
            loadMod(directories, metadata);
        } catch (InvalidModMetadataException ignored) {
        }
    }
}
