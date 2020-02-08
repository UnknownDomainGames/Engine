package engine.util;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUtils {

    public static List<Path> getDirectoriesInClassPath() {
        List<Path> paths = new ArrayList<>();
        for (String path : SystemUtils.JAVA_CLASS_PATH.split(File.pathSeparator)) {
            Path directory = Path.of(path);
            if (Files.isDirectory(directory)) {
                paths.add(directory);
            }
        }
        return List.copyOf(paths);
    }

    public static List<Path> getFilesInClassPath() {
        List<Path> paths = new ArrayList<>();
        for (String path : SystemUtils.JAVA_CLASS_PATH.split(File.pathSeparator)) {
            Path file = Path.of(path);
            if (!Files.isDirectory(file)) {
                paths.add(file);
            }
        }
        return List.copyOf(paths);
    }
}
