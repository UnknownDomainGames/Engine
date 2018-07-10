package unknowndomain.engine.api.unclassified;

import java.io.IOException;
import java.net.URL;

public interface ResourceSource {
    Resource load(URL path) throws IOException;

    PackInfo info() throws IOException;


    String type();
}
