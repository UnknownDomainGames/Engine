package nullengine.mod.dummy;

import com.google.common.base.Strings;
import nullengine.event.EventBus;
import nullengine.mod.ModAssets;
import nullengine.mod.ModContainer;
import nullengine.mod.ModMetadata;
import nullengine.util.versioning.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;

public class DummyModContainer implements ModContainer {

    private final ModMetadata descriptor;
    private final Logger logger;
    private Object modInstance;
    private ClassLoader classloader;
    private Collection<Path> sources;
    private Path configPath;
    private Path dataPath;
    private ModAssets assets;
    private EventBus eventbus;

    public DummyModContainer(ModMetadata descriptor) {
        this.descriptor = descriptor;
        this.logger = LoggerFactory.getLogger(Strings.isNullOrEmpty(descriptor.getName()) ? descriptor.getId() : descriptor.getName());
    }

    @Override
    public String getId() {
        return descriptor.getId();
    }

    @Override
    public Version getVersion() {
        return descriptor.getVersion();
    }

    @Override
    public Object getInstance() {
        return modInstance;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classloader;
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
        return eventbus;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ModMetadata getMetadata() {
        return descriptor;
    }

    public DummyModContainer setInstance(Object modInstance) {
        this.modInstance = modInstance;
        return this;
    }

    public DummyModContainer setClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
        return this;
    }

    public DummyModContainer setSources(Collection<Path> sources) {
        this.sources = sources;
        return this;
    }

    public DummyModContainer setConfigPath(Path configPath) {
        this.configPath = configPath;
        return this;
    }

    public DummyModContainer setDataPath(Path dataPath) {
        this.dataPath = dataPath;
        return this;
    }

    public DummyModContainer setAssets(ModAssets assets) {
        this.assets = assets;
        return this;
    }

    public DummyModContainer setEventBus(EventBus eventbus) {
        this.eventbus = eventbus;
        return this;
    }
}
