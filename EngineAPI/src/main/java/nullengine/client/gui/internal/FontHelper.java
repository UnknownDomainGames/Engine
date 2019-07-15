package nullengine.client.gui.internal;

import nullengine.client.gui.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FontHelper {

    Font getDefaultFont();

    List<Font> getAvailableFonts();

    Font loadFont(Path path) throws IOException;

    Font loadFont(Path path, float size) throws IOException;

    Font loadFont(InputStream in, float size) throws IOException;

    float computeTextWidth(CharSequence text, Font font);

    float computeTextHeight(CharSequence text, Font font);
}
