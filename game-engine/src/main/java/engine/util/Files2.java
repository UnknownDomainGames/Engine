package engine.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public final class Files2 {

    /**
     * This method is for fixing bugs (Relates to <a target="_blank" href="https://bugs.openjdk.java.net/browse/JDK-8024496">JDK-8024496</a>, JDK-8029608).
     * <br>
     * Call {@link Files#createDirectories(Path, FileAttribute[])} throws AccessDeniedException after called {@link Files#delete(Path)}
     */
    public static boolean createDirectories(Path path) {
        return createDirectories(path.toFile());
    }

    public static boolean createDirectories(File file) {
        return file.mkdirs();
    }

    public static boolean deleteDirectoryIfPresent(Path path) {
        return deleteDirectoryIfPresent(path.toFile());
    }

    public static boolean deleteDirectoryIfPresent(File file) {
        if (!file.exists()) {
            return false;
        }
        deleteDirectory(file);
        return true;
    }

    public static boolean deleteDirectory(Path path) {
        return deleteDirectory(path.toFile());
    }

    public static boolean deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteDirectory(child);
                }
            }
        }
        return file.delete();
    }

    private Files2() {
    }
}
