package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.mod.ModContainer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class ModAssetSource extends FileSystemAssetSource {

    @Nonnull
    public static ModAssetSource create(@Nonnull ModContainer modContainer) throws IOException {
        requireNonNull(modContainer);
        requireNonNull(modContainer.getSource(), "Unknown mod source.");
        Path path = modContainer.getSource();
        if (Files.isDirectory(path)) {
            return new ModAssetSource(modContainer, FileSystems.getDefault(), path.resolve("assets").toString());
        } else {
            FileSystem fileSystem = FileSystems.newFileSystem(path, modContainer.getClassLoader());
            return new ModAssetSource(modContainer, fileSystem, "assets");
        }
    }

    private final ModContainer modContainer;

    protected ModAssetSource(ModContainer modContainer, FileSystem fileSystem, String root) {
        super(fileSystem, root);
        this.modContainer = modContainer;
    }

    public ModContainer getModContainer() {
        return modContainer;
    }
}
