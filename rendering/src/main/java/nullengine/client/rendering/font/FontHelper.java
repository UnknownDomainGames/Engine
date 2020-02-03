package nullengine.client.rendering.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public interface FontHelper {

    Font getDefaultFont();

    List<Font> getAvailableFonts();

    boolean isAvailableFont(Font font);

    Font loadFont(Path path) throws IOException;

    Font loadFont(Path path, float size) throws IOException;

    Font loadFont(InputStream input) throws IOException;

    Font loadFont(InputStream input, float size) throws IOException;

    default float computeTextWidth(String text, Font font) throws UnavailableFontException {
        return computeTextWidth(text, font, -1);
    }

    List<String> wrapText(String text, float width, Font font);

    float computeTextWidth(String text, Font font, float ceilingWidth) throws UnavailableFontException;

    default float computeTextHeight(String text, Font font) throws UnavailableFontException {
        return computeTextHeight(text, font, -1);
    }

    default float computeTextHeight(String text, Font font, float ceilingWidth) throws UnavailableFontException {
        return computeTextHeight(text, font, ceilingWidth, 1.0f);
    }

    float computeTextHeight(String text, Font font, float ceilingWidth, float leading) throws UnavailableFontException;

    TextMesh bakeTextMesh(CharSequence text, Font font);

    static FontHelper instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<FontHelper> instance = () -> {
            throw new IllegalStateException("FontHelper is not initialized");
        };

        public static void setInstance(FontHelper instance) {
            Internal.instance = () -> instance;
        }
    }
}
