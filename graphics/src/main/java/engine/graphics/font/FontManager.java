package engine.graphics.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

public abstract class FontManager {
    private static FontManager instance;

    public static synchronized void initialize(String factoryName) {
        if (instance != null) throw new IllegalStateException("FontManager has been initialized");
        instance = ServiceLoader.load(FontManager.Factory.class)
                .stream()
                .filter(provider -> provider.get().getName().equals(factoryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No found FontManager: " + factoryName))
                .get()
                .create();
    }

    public static FontManager instance() {
        return instance;
    }

    public abstract Font getDefaultFont();

    public abstract List<Font> getAvailableFonts();

    public abstract boolean isAvailableFont(Font font);

    public abstract Font loadFont(Path path) throws IOException;

    public abstract Font loadFont(Path path, float size) throws IOException;

    public abstract Font loadFont(InputStream input) throws IOException;

    public abstract Font loadFont(InputStream input, float size) throws IOException;

    public abstract String getFontFamily(Font font, Locale locale);

    public abstract String getFontName(Font font, Locale locale);

    public float computeTextWidth(String text, Font font) throws UnavailableFontException {
        return computeTextWidth(text, font, -1);
    }

    public abstract List<String> wrapText(String text, float width, Font font);

    public abstract float computeTextWidth(String text, Font font, float ceilingWidth) throws UnavailableFontException;

    public float computeTextHeight(String text, Font font) throws UnavailableFontException {
        return computeTextHeight(text, font, -1);
    }

    public float computeTextHeight(String text, Font font, float ceilingWidth) throws UnavailableFontException {
        return computeTextHeight(text, font, ceilingWidth, 1.0f);
    }

    public abstract float computeTextHeight(String text, Font font, float ceilingWidth, float leading) throws UnavailableFontException;

    public abstract TextMesh bakeTextMesh(CharSequence text, Font font);

    public interface Factory {
        String getName();

        FontManager create();
    }
}
