package unknowndomain.engine.mod;

import java.nio.file.Path;

public class LoadableMod {

    private final Path source;
    private final ModMetadata metadata;

    public LoadableMod(Path source, ModMetadata metadata) {
        this.source = source;
        this.metadata = metadata;
    }

    public Path getSource() {
        return source;
    }

    public ModMetadata getMetadata() {
        return metadata;
    }
}
