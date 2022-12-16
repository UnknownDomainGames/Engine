package engine.mod.java;

import engine.event.EventBus;
import engine.event.SimpleEventBus;
import engine.mod.ModAssets;
import engine.mod.ModContainer;
import engine.mod.ModMetadata;
import engine.util.versioning.Version;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collection;

public class JavaModContainer implements ModContainer {

    private final Collection<Path> sources;
    private final ClassLoader classLoader;
    private final ModMetadata metadata;
    private final Path configPath;
    private final Path dataPath;
    private final ModAssets assets;
    private final EventBus eventBus;
    private final Logger logger;
    private final Object instance;

    public JavaModContainer(Collection<Path> sources, ClassLoader classLoader, ModMetadata metadata, Path configPath, Path dataPath, ModAssets assets, Logger logger, Object instance) {
        this.sources = sources;
        this.classLoader = classLoader;
        this.metadata = metadata;
        this.configPath = configPath;
        this.dataPath = dataPath;
        this.assets = assets;
        this.eventBus = new SimpleEventBus();
        this.logger = logger;
        this.instance = instance;
    }

    @Override
    public String getId() {
        return metadata.getId();
    }

    @Override
    public Version getVersion() {
        return metadata.getVersion();
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Collection<Path> getSources() {
        return sources;
    }

    @Override
    public Path getConfigPath() {
        return configPath;
    }

    @Override
    public Path getDataPath() {
        return dataPath;
    }

    @Override
    public ModAssets getAssets() {
        return assets;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }
}
