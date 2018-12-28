package unknowndomain.engine.client.asset;

import java.io.InputStream;

public interface AssetSource {

    boolean has(AssetPath path);

    InputStream openStream(AssetPath path);
}
