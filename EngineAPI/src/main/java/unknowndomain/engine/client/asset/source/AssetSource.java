package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface AssetSource {

    /**
     * @param path
     * @return if path is not exists, return null.
     */
    @Nullable
    Path toPath(AssetPath path);

    default boolean exists(AssetPath path) {
        return Files.exists(toPath(path));
    }

    default InputStream openStream(AssetPath path) throws IOException {
        return Files.newInputStream(toPath(path));
    }
}
