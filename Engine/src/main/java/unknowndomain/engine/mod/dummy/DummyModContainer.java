package unknowndomain.engine.mod.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.mod.ModAssets;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.util.versioning.Version;

import java.nio.file.Path;

public class DummyModContainer implements ModContainer {

    private final ModDescriptor descriptor;
    private final Logger logger;
    private Object modInstance;
    private ClassLoader classloader;
    private Path source;
    private ModAssets assets;
    private EventBus eventbus;

    public DummyModContainer(ModDescriptor descriptor) {
        this.descriptor = descriptor;
        this.logger = LoggerFactory.getLogger(descriptor.getModId());
    }

    @Override
    public String getModId() {
        return descriptor.getModId();
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
    public Path getSource() {
        return source;
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
    public ModDescriptor getDescriptor() {
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

    public DummyModContainer setSource(Path source) {
        this.source = source;
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
