package unknowndomain.engine.client.asset.source;

import com.google.common.base.Strings;
import unknowndomain.engine.client.asset.AssetPath;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class FileSystemAssetSource implements AssetSource {

    private final FileSystem fileSystem;
    private final String root;

    public FileSystemAssetSource(@Nonnull FileSystem fileSystem, String root) {
        this.fileSystem = requireNonNull(fileSystem);
        this.root = Strings.nullToEmpty(root);
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
        return fileSystem.getPath(root, path.getRealPath());
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getRoot() {
        return root;
    }

    @Override
    public void dispose() {
        try {
            fileSystem.close();
        } catch (IOException ignored) {
        }
    }
}
