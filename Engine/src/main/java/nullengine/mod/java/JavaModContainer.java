package nullengine.mod.java;

import nullengine.event.EventBus;
import nullengine.event.SimpleEventBus;
import nullengine.event.reflect.ReflectEventListenerFactory;
import nullengine.mod.ModAssets;
import nullengine.mod.ModContainer;
import nullengine.mod.ModMetadata;
import nullengine.util.versioning.Version;
import org.slf4j.Logger;

import javax.net.ssl.X509KeyManager;
import java.nio.file.Path;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collection;

public class JavaModContainer implements ModContainer {

    private final Collection<Path> sources;
    private final ClassLoader classLoader;
    private final ModMetadata metadata;
    private final ModAssets assets;
    private final EventBus eventBus;
    private final Logger logger;
    private final Object instance;

    public JavaModContainer(Collection<Path> sources, ClassLoader classLoader, ModMetadata metadata, ModAssets assets, Logger logger, Object instance) {
        this.sources = sources;
        this.classLoader = classLoader;
        this.metadata = metadata;
        this.assets = assets;
        this.eventBus = SimpleEventBus.builder().eventListenerFactory(ReflectEventListenerFactory.instance()).build();
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
