package unknowndomain.engine.mod.java.dev;

import unknowndomain.engine.mod.impl.AbstractModAssets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public class DevModAssets extends AbstractModAssets {

    private final Collection<Path> roots;

    public DevModAssets(Collection<Path> roots) {
        this.roots = roots;
    }

    @Override
    public Optional<Path> get(String first) {
        for (Path root : roots) {
            Path path = root.resolve(first);
            if (Files.exists(path)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Path> get(String first, String... more) {
        for (Path root : roots) {
            Path path = root.resolve(Path.of(first, more));
            if (Files.exists(path)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }
}
