package unknowndomain.engine.mod.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class ModCollector {

    public static Iterator<Path> createFolderModCollector(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            throw new IllegalStateException("Path is not exists.");
        }

        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Path must be directory.");
        }

        return Files.find(folder, 1, (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".jar")).iterator();
    }
}
