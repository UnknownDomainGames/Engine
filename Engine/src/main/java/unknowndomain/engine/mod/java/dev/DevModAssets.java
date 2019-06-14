package unknowndomain.engine.mod.java.dev;

import unknowndomain.engine.mod.impl.AbstractModAssets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class DevModAssets extends AbstractModAssets {

    private final Collection<Path> roots;

    public DevModAssets(Collection<Path> roots) {
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

    @Override
    public boolean exists(String first) {
        for (Path root : roots) {
            Path path = root.resolve(first);
            if (Files.exists(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean exists(String first, String... more) {
        for (Path root : roots) {
            Path path = root.resolve(Path.of(first, more));
            if (Files.exists(path)) {
                return true;
            }
        }
        return false;
    }
}
