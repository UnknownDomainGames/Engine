package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.DependencyManager;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModMetadataFinder;
import unknowndomain.engine.mod.exception.InvalidModMetadataException;
import unknowndomain.engine.mod.java.JavaModLoader;

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
        try {
            ModMetadata metadata = modMetadataFinder.find(directories);
            loadMod(directories, metadata);
        } catch (InvalidModMetadataException ignored) {
        }
    }
}
