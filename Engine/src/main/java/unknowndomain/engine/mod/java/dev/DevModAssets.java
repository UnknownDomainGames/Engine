package unknowndomain.engine.mod.java.dev;

import unknowndomain.engine.mod.java.JavaModAssets;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class DevModAssets extends JavaModAssets {

    private final Set<Path> roots;

    public DevModAssets(FileSystem fileSystem, Set<Path> roots) {
        super(fileSystem);
        this.roots = roots;
    }

    @Override
    public Path get(String first) {
        for (Path root : roots) {
            Path path = root.resolve(first);
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }

    @Override
    public Path get(String first, String... more) {
        for (Path root : roots) {
            Path path = root.resolve(Path.of(first, more));
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }
}
