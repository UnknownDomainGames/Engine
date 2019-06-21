package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.SimpleEventBus;
import unknowndomain.engine.event.reflect.ReflectEventListenerFactory;
import unknowndomain.engine.mod.ModAssets;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.util.versioning.Version;

import java.nio.file.Path;

public class JavaModContainer implements ModContainer {

    private final ModMetadata descriptor;
    private final ClassLoader classLoader;
    private final ModAssets assets;
    private final EventBus eventBus;
    private final Logger logger;
    private final Object instance;

    public JavaModContainer(ModMetadata descriptor, ClassLoader classLoader, ModAssets assets, Logger logger, Object instance) {
        this.descriptor = descriptor;
        this.classLoader = classLoader;
        this.assets = assets;
        this.eventBus = SimpleEventBus.builder().eventListenerFactory(ReflectEventListenerFactory.instance()).build();
        this.logger = logger;
        this.instance = instance;
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
        return descriptor;
    }
}
