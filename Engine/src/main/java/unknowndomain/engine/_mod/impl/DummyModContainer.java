package unknowndomain.engine._mod.impl;

import org.slf4j.Logger;
import unknowndomain.engine._mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;

import java.nio.file.Path;

public class DummyModContainer implements ModContainer {
    @Override
    public String getModId() {
        return null;
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
        return null;
    }

    @Override
    public ModMetadata getMetadata() {
        return null;
    }
}
