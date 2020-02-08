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

public class PackedAssetSource extends FileSystemAssetSource {

    @Nonnull
    public static PackedAssetSource create(@Nonnull Path path) throws IOException {
        requireNonNull(path);

        FileSystem fileSystem;
        Path root;
        if (Files.isDirectory(path)) {
            fileSystem = FileSystems.getDefault();
            root = path.resolve("assets");
        } else {
            fileSystem = FileSystems.newFileSystem(path, PackedAssetSource.class.getClassLoader());
            root = fileSystem.getPath("assets");
        }

        Path assetMetadataPath = root.resolve("assetpack.json");
        PackedAssetMetadata metadata;
        try (Reader reader = new InputStreamReader(Files.newInputStream(assetMetadataPath))) {
            metadata = PackedAssetMetadata.fromJson(JsonUtils.parser().parse(reader).getAsJsonObject());
        }
        return new PackedAssetSource(fileSystem, root.toString(), path, metadata);
    }

    private final Path source;
    private final PackedAssetMetadata metadata;

    public PackedAssetSource(@Nonnull FileSystem fileSystem, String root, Path source, PackedAssetMetadata metadata) {
        super(fileSystem, root);
        this.source = source;
        this.metadata = metadata;
    }

    public Path getSource() {
        return source;
    }

    public PackedAssetMetadata getMetadata() {
        return metadata;
    }
}
