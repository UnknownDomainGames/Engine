package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    ResourcePath location();

    InputStream open() throws IOException;

    byte[] cache() throws IOException;

    void invalidate();

    interface Manifest {
        Manifest require(ResourcePath... path);
    }

    // not impl yet
    // this api need more discussion...
    // Map<String, String> metadata();
}
