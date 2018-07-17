package unknowndomain.engine.api.resource;

import unknowndomain.engine.api.util.DomainedPath;

import java.io.IOException;

public interface ResourceSource {
    Resource load(DomainedPath path) throws IOException;

    PackInfo info() throws IOException;

    String type();
}
