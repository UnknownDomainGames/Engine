package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceSourceBuiltin implements ResourceSource {
    @Override
    public Resource load(ResourcePath path) throws IOException {
        String absPath = String.format("/assets/%s", path.getPath());
        final URL url = ResourceSourceBuiltin.class.getResource(absPath);
        if (url == null) return null;
        return new ResourceBase(path) {
            @Override
            public InputStream open() throws IOException {
                return url.openStream();
            }
        };
    }

    @Override
    public PackInfo info() throws IOException {
        return null;
    }

    @Override
    public String type() {
        return null;
    }
}
