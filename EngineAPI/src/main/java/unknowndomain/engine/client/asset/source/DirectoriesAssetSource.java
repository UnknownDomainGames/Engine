package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public class DirectoriesAssetSource implements AssetSource {

    private final Collection<Path> roots;

    public DirectoriesAssetSource(Collection<Path> roots) {
        this.roots = roots;
    }

    @Override
    public Optional<Path> toPath(AssetPath path) {
        for (Path root : roots) {
            Path _path = root.resolve(path.getRealPath());
            if (Files.exists(_path)) {
                return Optional.of(_path);
            }
        }
        return Optional.empty();
    }
}
