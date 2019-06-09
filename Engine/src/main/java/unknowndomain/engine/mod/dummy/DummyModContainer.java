package unknowndomain.engine.mod.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.mod.ModAssets;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.nio.file.Path;

public class DummyModContainer implements ModContainer {

    private final ModDescriptor descriptor;
    private final Logger logger;

    public DummyModContainer(ModDescriptor descriptor) {
        this.descriptor = descriptor;
        this.logger = LoggerFactory.getLogger(descriptor.getModId());
    }

    @Override
    public String getModId() {
        return descriptor.getModId();
    }

    @Override
    public ComparableVersion getVersion() {
        return descriptor.getVersion();
    }

    @Override
    public Object getInstance() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Path getSource() {
        return null;
    }

    @Override
    public ModAssets getAssets() {
        return null;
    }

    @Override
    public EventBus getEventBus() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ModDescriptor getDescriptor() {
        return descriptor;
    }
}
