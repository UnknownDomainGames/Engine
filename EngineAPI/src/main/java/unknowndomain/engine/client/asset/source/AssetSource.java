package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.util.Disposable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface AssetSource extends Disposable {

    boolean has(AssetPath path);

    InputStream openStream(AssetPath path) throws IOException;

    Path toPath(AssetPath path);
}
