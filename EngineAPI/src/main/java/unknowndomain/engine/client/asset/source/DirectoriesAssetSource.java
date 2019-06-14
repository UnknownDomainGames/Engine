package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class DirectoriesAssetSource implements AssetSource {

    private final Collection<Path> roots;

    public DirectoriesAssetSource(Collection<Path> roots) {
        this.roots = roots;
    }

    @Override
    public boolean exists(AssetPath path) {
        return Files.exists(toPath(path));
    }

    @Override
    public InputStream openStream(AssetPath path) throws IOException {
        return Files.newInputStream(toPath(path));
    }

    @Override
    public Path toPath(AssetPath path) {
        for (Path root : roots) {
            Path _path = root.resolve(path.getRealPath());
            if (Files.exists(_path)) {
                return _path;
            }
        }
        return null;
    }
}
