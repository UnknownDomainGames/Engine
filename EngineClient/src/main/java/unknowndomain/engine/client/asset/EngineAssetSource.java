package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.FileSystemAssetSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

public final class EngineAssetSource extends FileSystemAssetSource {

    public static EngineAssetSource create() {
        try {
            Path path = Paths.get(EngineAssetSource.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isDirectory(path)) {
                return new EngineAssetSource(FileSystems.getDefault(), Paths.get(EngineAssetSource.class.getClassLoader().getResource("assets/engine").toURI()).toAbsolutePath().getParent().toString());
            } else {
                FileSystem fileSystem = FileSystems.newFileSystem(path, EngineAssetSource.class.getClassLoader());
                return new EngineAssetSource(fileSystem, "assets");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e); // TODO: log it
        }
    }

    private EngineAssetSource(FileSystem fileSystem, String root) {
        super(fileSystem, root);
    }
}
