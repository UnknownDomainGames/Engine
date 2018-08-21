package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.io.InputStream;

public class ResourceSourceBuiltin implements ResourceSource {
    @Override
    public boolean has(String path) {
        String absPath = String.format("/assets/%s", path);
        return ResourceSourceBuiltin.class.getResource(absPath) != null;
    }

    @Override
    public InputStream open(String path) throws IOException {
        String absPath = String.format("/assets/%s", path);
        return ResourceSourceBuiltin.class.getResourceAsStream(absPath);
    }

    @Override
    public ResourceSourceInfo info() throws IOException {
        return null;
    }

    @Override
    public String type() {
        return null;
    }
}
