package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.texture.GLTexture;

public interface Graphics {

    int getColor();

    void setColor(int color);

    void drawLine(int x1, int y1, int x2, int y2);

    void drawRect(int x, int y, int width, int height);

    void fillRect(int x, int y, int width, int height);

    void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    void drawText(CharSequence text, int x, int y);

    void drawTexture(GLTexture texture, int x, int y, int width, int height);
}
