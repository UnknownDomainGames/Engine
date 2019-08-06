package nullengine.client.asset.source;

import java.nio.file.Path;
import java.util.Optional;

public interface AssetSource {

    /**
     * @param url
     * @return if resource is not exists, return {@link Optional#empty()}.
     */
    Optional<Path> getPath(String url);
}
