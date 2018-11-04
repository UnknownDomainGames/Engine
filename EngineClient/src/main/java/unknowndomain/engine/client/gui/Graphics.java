package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.util.Color;

public interface Graphics {

    Color getColor();

    void setColor(Color color);

    void drawLine(float x1, float y1, float x2, float y2);

    void drawRect(float x, float y, float width, float height);

    void fillRect(float x, float y, float width, float height);

    void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    /**
     * Draw a quadratic Belzier curve
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param px
     * @param py
     */
    void drawQuad(float startX, float startY, float endX, float endY, float px, float py);

    /**
     * Draw a Belazier curve
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     */
    void drawCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2);

    /**
     * Draw a elliptical arc
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param radiusX
     * @param radiusY
     * @param xAxisRotation
     * @param largeArcFlag
     * @param sweepFlag
     */
    void drawArc(float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag);

    void drawText(CharSequence text, float x, float y);

    void drawTexture(GLTexture texture, float x, float y, float width, float height, float u, float v);
}
