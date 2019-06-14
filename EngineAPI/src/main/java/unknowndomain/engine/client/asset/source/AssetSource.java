package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.nio.file.Path;
import java.util.Optional;

public interface AssetSource {

    /**
     * @param path
     * @return if path is not exists, return {@link Optional#empty()}.
     */
    Optional<Path> toPath(AssetPath path);
}
