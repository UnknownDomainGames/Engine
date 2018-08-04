package unknowndomain.engine.client.resource;

import java.io.IOException;

public interface ResourceManager {
    void clearCache();

    void clearAll();

    void invalidate(ResourcePath path);

    void addResourceSource(ResourceSource source);

    Resource load(ResourcePath location) throws IOException;
}
