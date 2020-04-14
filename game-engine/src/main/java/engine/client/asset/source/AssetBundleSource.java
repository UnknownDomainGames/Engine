package engine.client.asset.source;

import engine.util.JsonUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public final class AssetBundleSource extends FileSystemAssetSource {

    private final Path source;
    private final AssetBundleMetadata metadata;

    @Nonnull
    public static AssetBundleSource create(@Nonnull Path path) throws IOException {
        requireNonNull(path);

        FileSystem fileSystem;
        Path root;
        if (Files.isDirectory(path)) {
            fileSystem = FileSystems.getDefault();
            root = path.resolve("asset");
        } else {
            fileSystem = FileSystems.newFileSystem(path, Thread.currentThread().getContextClassLoader());
            root = fileSystem.getPath("asset");
        }

        Path assetMetadataPath = root.resolve("asset_bundle.json");
        AssetBundleMetadata metadata;
        try (Reader reader = new InputStreamReader(Files.newInputStream(assetMetadataPath))) {
            metadata = JsonUtils.gson().fromJson(reader, AssetBundleMetadata.class);
        }
        return new AssetBundleSource(fileSystem, root.toString(), path, metadata);
    }

    public AssetBundleSource(@Nonnull FileSystem fileSystem, String root, Path source, AssetBundleMetadata metadata) {
        super(fileSystem, root);
        this.source = source;
        this.metadata = metadata;
    }

    public Path getSource() {
        return source;
    }

    public AssetBundleMetadata getMetadata() {
        return metadata;
    }
}
