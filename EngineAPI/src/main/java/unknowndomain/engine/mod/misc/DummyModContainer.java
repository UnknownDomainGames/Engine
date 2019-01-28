package unknowndomain.engine.mod.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;

import java.nio.file.Path;

public class DummyModContainer implements ModContainer {

    private final Logger logger;
    private final ModDescriptor descriptor;

    public DummyModContainer(ModDescriptor descriptor) {
        this.descriptor = descriptor;
        this.logger = LoggerFactory.getLogger(descriptor.getModId());
    }

    @Override
    public String getModId() {
        return getDescriptor().getModId();
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
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ModDescriptor getDescriptor() {
        return descriptor;
    }
}
