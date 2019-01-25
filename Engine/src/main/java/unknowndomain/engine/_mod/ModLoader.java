package unknowndomain.engine._mod;

import java.nio.file.Path;

public interface ModLoader {

    ModContainer load(Path path);
}
