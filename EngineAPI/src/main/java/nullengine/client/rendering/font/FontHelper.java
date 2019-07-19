package nullengine.client.rendering.font;

import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.exception.UninitializationException;

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

    float computeTextWidth(CharSequence text, Font font) throws UnavailableFontException;

    float computeTextHeight(CharSequence text, Font font) throws UnavailableFontException;

    void renderText(GLBuffer buffer, CharSequence text, Font font, int color, Runnable renderer) throws UnavailableFontException;

    static FontHelper instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<FontHelper> instance = UninitializationException.supplier("FontHelper is uninitialized");

        public static void setInstance(FontHelper instance) {
            Internal.instance = () -> instance;
        }
    }
}
