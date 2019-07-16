package nullengine.client.gui.internal;

import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.util.buffer.GLBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FontHelper {

    Font getDefaultFont();

    List<Font> getAvailableFonts();

    Font loadFont(Path path) throws IOException;

    Font loadFont(Path path, float size) throws IOException;

    Font loadFont(InputStream input) throws IOException;

    Font loadFont(InputStream input, float size) throws IOException;

    float computeTextWidth(CharSequence text, Font font);

    float computeTextHeight(CharSequence text, Font font);

    void bindTexture(Font font);

    void generateMesh(GLBuffer buffer, CharSequence text, Font font, int color);
}
