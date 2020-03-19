package engine.mod.impl;

import engine.Engine;
import engine.Platform;
import engine.event.SimpleEventBus;
import engine.event.reflect.ReflectEventListenerFactory;
import engine.mod.ModContainer;
import engine.mod.ModLoader;
import engine.mod.ModManager;
import engine.mod.ModMetadata;
import engine.mod.dummy.DummyModContainer;
import engine.mod.exception.InvalidModMetadataException;
import engine.mod.exception.MissingDependencyException;
import engine.mod.exception.ModAlreadyLoadedException;
import engine.mod.exception.ModLoadException;
import engine.mod.init.ModInitializer;
import engine.mod.java.JavaModAssets;
import engine.mod.java.JavaModLoader;
import engine.mod.java.ModClassLoader;
import engine.util.RuntimeEnvironment;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static engine.util.ClassPathUtils.getDirectoriesInClassPath;
import static engine.util.ClassPathUtils.getFilesInClassPath;
import static engine.util.RuntimeEnvironment.MOD_DEVELOPMENT;
import static java.lang.String.format;

public class EngineModManager implements ModManager {

    private static final Pattern MOD_ID_PATTERN = Pattern.compile("[a-z0-9]+");

    private final Map<String, ModContainer> loadedModContainers = new LinkedHashMap<>();

    private final ModLoader modLoader = new JavaModLoader();

    private final ModMetadataFinder modMetadataFinder = new ModMetadataFinder();
    private final DependencyManager dependencyManager = new DependencyManager(this);
    private final ModInitializer modInitializer;

    private final Engine engine;

    private final Path modConfigsPath;
    private final Path modDatasPath;

    public EngineModManager(Engine engine, Path modConfigsPath, Path modDatasPath) {
        this.engine = engine;
        this.modConfigsPath = modConfigsPath;
        this.modDatasPath = modDatasPath;
        this.modInitializer = new ModInitializer(engine);
        loadEngineDummyMod();
    }

    @Override
    public Optional<ModContainer> getMod(String id) {
        return Optional.ofNullable(loadedModContainers.get(id));
    }

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
        var modCandidates = dependencyManager.sortModCandidates(collectMods());
        for (ModCandidate modCandidate : modCandidates) {
            var mod = loadMod(modCandidate);
            modInitializer.init(mod);
        }
    }

    private ModContainer loadMod(ModCandidate modCandidate) {
        var metadata = modCandidate.getMetadata();
        if (!MOD_ID_PATTERN.matcher(metadata.getId()).matches()) {
            throw new ModLoadException(format("Illegal mod id \"%s\"", metadata.getId()));
        }

        if (isModLoaded(metadata.getId())) {
            throw new ModAlreadyLoadedException(metadata.getId());
        }

        var result = dependencyManager.checkDependencies(metadata.getDependencies());
        if (!result.isPassed()) {
            throw new MissingDependencyException(metadata.getId(), result);
        }

        modCandidate.setConfigPath(modConfigsPath.resolve(metadata.getId()));
        modCandidate.setDataPath(modDatasPath.resolve(metadata.getId()));

        ModContainer modContainer;
        try {
            modContainer = modLoader.load(modCandidate, dependencyManager.getDependentMods(metadata.getDependencies()));
        } catch (ModLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModLoadException(format("Cannot load mod \"%s\" because caught a exception.", metadata.getId()), e);
        }

        if (modContainer == null) {
            throw new ModLoadException(format("Cannot load mod \"%s\" because do not support load it.", metadata.getId()));
        }

        loadedModContainers.put(modContainer.getId(), modContainer);
        return modContainer;
    }

    private void loadEngineDummyMod() {
        try {
            var engineMod = new DummyModContainer(ModMetadata.builder().id("engine").version(Platform.getVersion()).name("Engine").build());
            var classLoader = engine.getClass().getClassLoader();
            engineMod.setClassLoader(classLoader);
            var engineJarPath = Path.of(engine.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            AbstractModAssets modAssets;
            if (engine.getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
                modAssets = new JavaModAssets(getDirectoriesInClassPath(), classLoader);
            } else {
                modAssets = new JavaModAssets(List.of(engineJarPath), classLoader);
            }
            modAssets.setMod(engineMod);
            engineMod.setAssets(modAssets);
            engineMod.setEventBus(SimpleEventBus.builder().eventListenerFactory(ReflectEventListenerFactory.instance()).build());
            engineMod.setConfigPath(modConfigsPath.resolve(engineMod.getId()));
            engineMod.setDataPath(modDatasPath.resolve(engineMod.getId()));
            loadedModContainers.put(engineMod.getId(), engineMod);
        } catch (IOException | URISyntaxException ignored) {
        }
    }

    public void initEngineDummyMod() {
        getMod("engine").ifPresent(modInitializer::init);
    }

    private List<ModCandidate> collectMods() {
        List<ModCandidate> collectedMods = new ArrayList<>();
        collectDirMods(collectedMods);
        collectDevEnvMods(collectedMods);
        return collectedMods;
    }

    private void collectDirMods(List<ModCandidate> collectedMods) {
        Path modFolder = engine.getRunPath().resolve("mods");
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
}
