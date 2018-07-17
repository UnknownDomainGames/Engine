package unknowndomain.engine.api.resource;

import unknowndomain.engine.api.util.DomainedPath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource {
    DomainedPath location();

    InputStream open() throws IOException;

    // not impl yet
    // this api need more discussion...
//    Map<String, String> metadata();
}
