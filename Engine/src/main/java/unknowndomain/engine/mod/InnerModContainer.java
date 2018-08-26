package unknowndomain.engine.mod;

import org.slf4j.Logger;

public class InnerModContainer implements ModContainer {
    private ModMetadata metadata;
    private Logger logger;
    private Object instance;

    public InnerModContainer(ModMetadata metadata, Logger logger, Object instance) {
        this.metadata = metadata;
        this.logger = logger;
        this.instance = instance;
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
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }
}
