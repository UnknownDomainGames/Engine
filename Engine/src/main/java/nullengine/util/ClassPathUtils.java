package nullengine.util;

import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUtils {

    private static List<Path> directories;
    private static List<Path> files;

    public static List<Path> getDirectoriesInClassPath() {
        if (directories == null) {
            List<Path> paths = new ArrayList<>();
            for (String path : SystemUtils.JAVA_CLASS_PATH.split(";")) {
                Path directory = Path.of(path);
                if (Files.isDirectory(directory)) {
                    paths.add(directory);
                }
            }
            directories = List.copyOf(paths);
        }
        return directories;
    }

    public static List<Path> getFilesInClassPath() {
        if (files == null) {
            List<Path> paths = new ArrayList<>();
            for (String path : SystemUtils.JAVA_CLASS_PATH.split(";")) {
                Path file = Path.of(path);
                if (!Files.isDirectory(file)) {
                    paths.add(file);
                }
            }
            files = List.copyOf(paths);
        }
        return files;
    }
}
