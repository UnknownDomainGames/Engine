package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface AssetSource {

    boolean exists(AssetPath path);

    InputStream openStream(AssetPath path) throws IOException;

    Path toPath(AssetPath path);
}
