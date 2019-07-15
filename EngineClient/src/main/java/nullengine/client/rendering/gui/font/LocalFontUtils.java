package nullengine.client.rendering.gui.font;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFontUtils {

    public static List<Path> findLocalFonts() {
        try {
            return Files.find(getLocalFontStoragePath(), 1, (path, basicFileAttributes) -> true).collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    public static Path getLocalFontStoragePath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Path.of("C:\\Windows\\Fonts").toAbsolutePath();
        }
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        System.out.println(findLocalFonts());
    }
}
