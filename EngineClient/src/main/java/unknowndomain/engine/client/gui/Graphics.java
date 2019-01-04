package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.util.Color;

public interface Graphics {

    Color getColor();

    void setColor(Color color);

    Font getFont();

    void setFont(Font font);

    void drawLine(float x1, float y1, float x2, float y2);

    void drawRect(float x, float y, float width, float height);

    void fillRect(float x, float y, float width, float height);

    void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    /**
     * Draw a quadratic Belzier curve
     */
    void drawQuad(float startX, float startY, float endX, float endY, float px, float py);

    /**
     * Draw a Belazier curve
     */
    void drawCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2);

    /**
     * Draw a elliptical arc
     */
    void drawArc(float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag);

    // TODO: void drawPolygon(Polygon polygon);

    void drawText(CharSequence text, float x, float y);

    void drawTexture(GLTexture texture, float x, float y, float width, float height);

    void drawTexture(GLTexture texture, float x, float y, float width, float height, TextureUV textureUV);
}
