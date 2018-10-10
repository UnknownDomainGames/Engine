package unknowndomain.engine.client.rendering.gui.font;

import org.joml.Vector2f;

public interface FontRenderer {

    void drawText(CharSequence text, float x, float y, int color);

    Vector2f sizeText(CharSequence text);

}
