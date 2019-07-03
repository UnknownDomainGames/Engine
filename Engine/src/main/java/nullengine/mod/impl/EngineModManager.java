package nullengine.mod.impl;

import nullengine.Engine;
import nullengine.Platform;
import nullengine.mod.ModContainer;
import nullengine.mod.ModLoader;
import nullengine.mod.ModManager;
import nullengine.mod.dummy.DummyModContainer;
import nullengine.mod.exception.InvalidModMetadataException;
import nullengine.mod.exception.MissingDependencyException;
import nullengine.mod.exception.ModAlreadyLoadedException;
import nullengine.mod.exception.ModLoadException;
import nullengine.mod.java.JavaModAssets;
import nullengine.mod.java.JavaModLoader;
import nullengine.mod.java.ModClassLoader;
import nullengine.mod.misc.SimpleModMetadata;
import nullengine.util.RuntimeEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static nullengine.util.ClassPathUtils.getDirectoriesInClassPath;
import static nullengine.util.ClassPathUtils.getFilesInClassPath;
import static nullengine.util.RuntimeEnvironment.MOD_DEVELOPMENT;

public class EngineModManager implements ModManager {

    private final Map<String, ModContainer> loadedModContainers = new LinkedHashMap<>();

    private final ModLoader modLoader = new JavaModLoader();

    private final ModMetadataFinder modMetadataFinder = new ModMetadataFinder();
    private final DependencyManager dependencyManager = new DependencyManager(this);

    private final Engine engine;

    public EngineModManager(Engine engine) {
        this.engine = engine;
        loadEngineDummyMod();
    }

    @Nullable
    @Override
    public Optional<ModContainer> getMod(String id) {
        return Optional.ofNullable(loadedModContainers.get(id));
    }

    @Nullable
    @Override
    public Optional<ModContainer> getMod(Class<?> clazz) {
        var classLoader = clazz.getClassLoader();
        return classLoader instanceof ModClassLoader ? Optional.of(((ModClassLoader) classLoader).getMod()) : Optional.empty();
    }

    @Override
    public boolean isModLoaded(String id) {
        return loadedModContainers.containsKey(id);
    }

    @Nonnull
    @Override
    public Collection<ModContainer> getLoadedMods() {
        return loadedModContainers.values();
    }

    public void loadMods() {
        List<ModCandidate> modCandidates = collectMods();

        dependencyManager.sortModCandidates(modCandidates);

        for (ModCandidate modCandidate : modCandidates) {
            loadMod(modCandidate);
        }
    }

    private void loadMod(ModCandidate modCandidate) {
        var metadata = modCandidate.getMetadata();
        if (isModLoaded(metadata.getId())) {
            throw new ModAlreadyLoadedException(metadata.getId());
        }

        var result = dependencyManager.checkDependencies(metadata.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(metadata.getId(), result);
        }

        ModContainer modContainer;
        try {
            modContainer = modLoader.load(modCandidate, dependencyManager.getDependentMods(metadata.getDependencies()));
        } catch (ModLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModLoadException(String.format("Cannot load mod \"%s\" because caught a exception.", metadata.getId()), e);
        }

        if (modContainer == null) {
            throw new ModLoadException(String.format("Cannot load mod \"%s\" because do not support load it.", metadata.getId()));
        }

        loadedModContainers.put(modContainer.getId(), modContainer);
    }

    private void loadEngineDummyMod() {
        try {
            var engineMod = new DummyModContainer(SimpleModMetadata.builder().id("engine").version(Platform.getVersion()).build());
            var classLoader = engine.getClass().getClassLoader();
            engineMod.setClassLoader(classLoader);
            Path engineJarPath = Path.of(engine.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            AbstractModAssets modAssets;
            if (engine.getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
                modAssets = new JavaModAssets(getDirectoriesInClassPath(), classLoader);
            } else {
                modAssets = new JavaModAssets(List.of(engineJarPath), classLoader);
            }
            modAssets.setMod(engineMod);
            engineMod.setAssets(modAssets);
            loadedModContainers.put(engineMod.getId(), engineMod);
        } catch (IOException | URISyntaxException ignored) {
        }
    }

    private List<ModCandidate> collectMods() {
        List<ModCandidate> collectedMods = new ArrayList<>();
        collectDirMods(collectedMods);
        collectDevEnvMods(collectedMods);
        return collectedMods;
    }

    private void collectDirMods(List<ModCandidate> collectedMods) {
        Path modFolder = Path.of("mods");
        if (!Files.exists(modFolder)) {
            try {
                Files.createDirectories(modFolder);
            } catch (IOException e) {
                Platform.getLogger().warn(e.getMessage(), e);
            }
        }

        try (var stream = Files.find(modFolder, 1,
                (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".jar"))) {
            stream.forEach(path -> {
                var sources = List.of(path);
                var metadata = modMetadataFinder.find(sources);
                collectedMods.add(new ModCandidate(sources, metadata));
            });
        } catch (IOException e) {
            // TODO: Crash report
        }
    }

    private void collectDevEnvMods(List<ModCandidate> collectedMods) {
        if (engine.getRuntimeEnvironment() != MOD_DEVELOPMENT) {
            return;
        }

        collectClassPathMods(collectedMods);
        collectWorkspaceMods(collectedMods);
    }

    private void collectClassPathMods(List<ModCandidate> collectedMods) {
        for (Path path : getFilesInClassPath()) {
            try {
                var sources = List.of(path);
                var metadata = modMetadataFinder.find(sources);
                collectedMods.add(new ModCandidate(sources, metadata));
            } catch (InvalidModMetadataException ignored) {
            }
        }
    }

    private void collectWorkspaceMods(List<ModCandidate> collectedMods) {
        try {
            var sources = getDirectoriesInClassPath();
            var metadata = modMetadataFinder.find(sources);
            collectedMods.add(new ModCandidate(sources, metadata));
        } catch (InvalidModMetadataException e) {
            Platform.getLogger().warn("Not found mod in workspace!");
        }
    }

//    public void loadMods() {
//        if (isModLoaded(modMetadata.getId())) {
//            throw new ModAlreadyLoadedException(modMetadata.getId());
//        }
//
//        DependencyCheckResult result = dependencyManager.checkDependencies(modMetadata.getDependencies());
//        if (!result.isPassed()) {
//            throw new MissingDependencyException(modMetadata.getId(), result);
//        }
//
//        ModContainer modContainer;
//        try {
//            modContainer = modLoader.load(sources, modMetadata, dependencyManager.getDependentMods(modMetadata.getDependencies()));
//        } catch (ModLoadException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new ModLoadException(modMetadata.getId(), e);
//        }
//
//        if (modContainer == null) {
//            throw new ModLoadException(modMetadata.getId());
//        }
//
//        loadedModContainers.put(modContainer.getId(), modContainer);
//        return modContainer;
//    }
}
