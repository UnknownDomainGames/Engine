package nullengine.client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public final class Files2 {

    public static boolean deleteIfPresent(Path path) throws IOException {
        if (!Files.exists(path)) {
            return false;
        }
        delete(path);
        return true;
    }

    public static void delete(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Iterator<Path> iterator = Files.list(path).iterator();
            while (iterator.hasNext()) {
                delete(iterator.next());
            }
        }
        Files.delete(path);
    }

    private Files2() {
    }
}
