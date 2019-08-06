package nullengine.client.asset.source;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class FileSystemAssetSource implements AssetSource {

    private final FileSystem fileSystem;
    private final String root;

    public FileSystemAssetSource(@Nonnull FileSystem fileSystem, String root) {
        this.fileSystem = requireNonNull(fileSystem);
        this.root = Strings.nullToEmpty(root);
    }

    @Override
    public Optional<Path> getPath(String url) {
        var path = fileSystem.getPath(root, url);
        return Files.exists(path) ? Optional.of(path) : Optional.empty();
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getRoot() {
        return root;
    }

    public void dispose() {
        try {
            fileSystem.close();
        } catch (IOException ignored) {
        }
    }
}
