package unknowndomain.engine.client.resource;

import unknowndomain.engine.api.resource.PackInfo;
import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceSource;
import unknowndomain.engine.api.util.DomainedPath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceSourceBuiltin implements ResourceSource {
    @Override
    public Resource load(DomainedPath path) throws IOException {
        String absPath = String.format("assets/%s", path.getPath());
        final URL url = ResourceSourceBuiltin.class.getResource(absPath);
        if (url == null) return null;
        return new Resource() {
            @Override
            public DomainedPath location() {
                return path;
            }

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
