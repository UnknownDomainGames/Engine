package unknowndomain.engine.mod.java;

import unknowndomain.engine.mod.impl.AbstractModAssets;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JavaModAssets extends AbstractModAssets {

    private FileSystem fileSystem;

    public JavaModAssets(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public Optional<Path> get(String first) {
        Path path = fileSystem.getPath(first);
        if (Files.notExists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    @Override
    public Optional<Path> get(String first, String... more) {
        Path path = fileSystem.getPath(first, more);
        if (Files.notExists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }
}
