package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.io.IOException;
import java.io.InputStream;

public interface AssetSource {

    boolean has(AssetPath path);

    InputStream openStream(AssetPath path) throws IOException;
}
