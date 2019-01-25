package unknowndomain.engine._mod.java;

import org.slf4j.Logger;
import unknowndomain.engine._mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;

import java.nio.file.Path;

public class JavaModContainer implements ModContainer {

    private final ModMetadata metadata;
    private final Path source;
    private final Object instance;
    private final ClassLoader classLoader;
    private final Logger logger;

    public JavaModContainer(ModMetadata metadata, Path source, Object instance, ClassLoader classLoader, Logger logger) {
        this.metadata = metadata;
        this.source = source;
        this.instance = instance;
        this.classLoader = classLoader;
        this.logger = logger;
    }

    @Override
    public String getModId() {
        return metadata.getId();
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
        return source;
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
