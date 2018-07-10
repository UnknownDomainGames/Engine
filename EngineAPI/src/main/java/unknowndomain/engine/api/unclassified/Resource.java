package unknowndomain.engine.api.unclassified;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource {
    URL location();

    InputStream open() throws IOException;

    // not impl yet
    // this api need more discussion...
//    Map<String, String> metadata();
}
