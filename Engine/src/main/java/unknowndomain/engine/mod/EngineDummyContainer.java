package unknowndomain.engine.mod;

import org.slf4j.Logger;
import unknowndomain.engine.Engine;

public class EngineDummyContainer implements ModContainer {

    private final ModMetadata metadata = ModMetadata.Builder.create().setId("engine").setVersion("0.0.1").setGroup("none").build();

    @Override
    public String getModId() {
        return metadata.getId();
    }

    @Override
    public Object getInstance() {
        return null;
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
