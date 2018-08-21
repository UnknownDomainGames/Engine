package unknowndomain.engine.mod;

import org.slf4j.Logger;

import java.nio.file.Path;

public interface ModContainer {
    String getModId();

    Object getInstance();

    Path getSource();

    Logger getLogger();

    boolean isEnable();

    void setEnable(boolean enable);

    ModMetadata getMetadata();

    ModState getState();
}
