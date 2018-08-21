package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceSource {
    boolean has(String path);

    InputStream open(String path) throws IOException;

    ResourceSourceInfo info() throws IOException;

    String type();
}
