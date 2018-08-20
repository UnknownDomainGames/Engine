package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    ResourcePath path();

    InputStream open() throws IOException;

    byte[] cache() throws IOException;

    void invalidate();

    // not impl yet
    // this api need more discussion...
    // Map<String, String> metadata();
}
