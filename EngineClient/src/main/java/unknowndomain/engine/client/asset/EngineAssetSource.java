package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.source.FileSystemAssetSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Objects;

public final class EngineAssetSource extends FileSystemAssetSource {

    public static EngineAssetSource create() {
        try {
            Path path = Paths.get(EngineAssetSource.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isDirectory(path)) {
                return new EngineAssetSource(FileSystems.getDefault(), resolveIdeResourcePath(path).toString());
            } else {
                FileSystem fileSystem = FileSystems.newFileSystem(path, EngineAssetSource.class.getClassLoader());
                return new EngineAssetSource(fileSystem, "assets");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e); // TODO: log it
        }
    }

    private static Path resolveIdeResourcePath(Path path) {
        Path tmp = path;
        Path gradleSrc = Path.of("");
        while(tmp.getParent() != null){
            tmp = tmp.getParent();
            if(Objects.equals(tmp.getFileName().toString(), "java")){
                gradleSrc = tmp.relativize(path);
            }
            if(Files.exists(tmp.resolveSibling("resources"))){
                return tmp.resolveSibling("resources").resolve(gradleSrc).resolve("assets");
            }
        }
        return path.getParent().resolve("resources").resolve(gradleSrc).resolve("assets"); //return default value
    }

    private EngineAssetSource(FileSystem fileSystem, String root) {
        super(fileSystem, root);
    }
}
