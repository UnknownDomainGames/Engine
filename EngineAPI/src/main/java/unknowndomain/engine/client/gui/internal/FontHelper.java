package unknowndomain.engine.client.gui.internal;

import unknowndomain.engine.client.gui.text.Font;

import java.io.IOException;
import java.io.InputStream;

public interface FontHelper {

    Font getDefaultFont();

    Font loadFont(InputStream in, float size) throws IOException;

    float computeTextWidth(CharSequence text, Font font);

    float computeTextHeight(CharSequence text, Font font);
}
