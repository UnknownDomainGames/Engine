package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.FileSystemAssetSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;

public final class EngineAssetSource extends FileSystemAssetSource {

    public static EngineAssetSource create() {
        try {
            Path path = Paths.get(EngineAssetSource.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isDirectory(path)) { // TODO: Development environment
                return new EngineAssetSource(FileSystems.getDefault(), path.getParent().resolve("resources").resolve("assets").toString());
            } else {
                FileSystem fileSystem = FileSystems.newFileSystem(EngineAssetSource.class.getProtectionDomain().getCodeSource().getLocation().toURI(), Collections.emptyMap(), EngineAssetSource.class.getClassLoader());
                return new EngineAssetSource(fileSystem, "assets");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e); // TODO: log it
        }
    }

    private EngineAssetSource(FileSystem fileSystem, String root) {
        super(fileSystem, root);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(create().has(AssetPath.of("unknowndomain")));
    }
}
