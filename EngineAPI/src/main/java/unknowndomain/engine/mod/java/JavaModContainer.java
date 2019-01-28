package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;

import java.nio.file.Path;

public class JavaModContainer implements ModContainer {

    private final ModDescriptor descriptor;
    private final ClassLoader classLoader;
    private final Logger logger;
    private final Object instance;

    public JavaModContainer(ModDescriptor descriptor, ClassLoader classLoader, Logger logger, Object instance) {
        this.descriptor = descriptor;
        this.classLoader = classLoader;
        this.logger = logger;
        this.instance = instance;
    }

    @Override
    public String getModId() {
        return descriptor.getModId();
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
    public Path getSource() {
        return descriptor.getSource();
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
