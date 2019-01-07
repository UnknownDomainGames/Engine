package unknowndomain.engine;

import org.slf4j.Logger;

import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModIdentifier;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.ModMetadata;

/**
 * ModContainer for UnknownDomain
 * 
 * @author iTNTPiston
 *
 */
public class UnknownDomain implements ModContainer, ModLoader {
    /**
     * self metadata
     */
    private ModMetadata metadata = ModMetadata.Builder.create().setId("unknowndomain").setVersion("0.0.1").setGroup("none").build();

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

    @Override
    public ModContainer load(ModIdentifier identifier) {
        if (metadata.equals(identifier)) {
            return this;
        }
        return null;
    }

}
