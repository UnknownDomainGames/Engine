package unknowndomain.engine.mod.java;

import unknowndomain.engine.mod.impl.AbstractModAssets;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaModAssets extends AbstractModAssets {

    private FileSystem fileSystem;

    public JavaModAssets(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public Path get(String first) {
        Path path = fileSystem.getPath(first);
        if (Files.notExists(path)) {
            return null;
        }
        return path;
    }

    @Override
    public Path get(String first, String... more) {
        Path path = fileSystem.getPath(first, more);
        if (Files.notExists(path)) {
            return null;
        }
        return path;
    }

    public boolean exists(String first) {
        return Files.exists(fileSystem.getPath(first));
    }

    public boolean exists(String first, String... more) {
        return Files.exists(fileSystem.getPath(first, more));
    }
}
