package unknowndomain.engine.client.asset;

import java.io.IOException;
import java.io.InputStream;

public interface AssetSource {

    boolean has(String path);

    InputStream openStream(String path) throws IOException;
}
