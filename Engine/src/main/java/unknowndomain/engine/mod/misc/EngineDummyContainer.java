package unknowndomain.engine.mod.misc;

import org.slf4j.Logger;
import unknowndomain.engine.Engine;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;

public class EngineDummyContainer implements ModContainer {

    private final ModMetadata metadata = ModMetadata.Builder.create().id("engine").version(Platform.getVersion()).build();

    @Override
    public String getModId() {
        return metadata.getId();
    }

    @Override
    public Object getInstance() {
        return this;
    }

    @Override
    public Logger getLogger() {
        return Engine.LOGGER;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }
}
