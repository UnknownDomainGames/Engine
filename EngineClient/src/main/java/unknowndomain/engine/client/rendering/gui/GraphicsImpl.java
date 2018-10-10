package unknowndomain.engine.client.rendering.gui;

import unknowndomain.engine.client.gui.Graphics;
import unknowndomain.engine.client.rendering.gui.font.FontRenderer;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.util.BufferBuilder;

import static org.lwjgl.opengl.GL11.*;

public class GraphicsImpl implements Graphics {

    private final Tessellator tessellator = Tessellator.getInstance();
    private final FontRenderer fontRenderer;

    private int color;
    private float r;
    private float g;
    private float b;
    private float a;

    public GraphicsImpl(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        setColor(0xFFFFFFFF); // White
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
        r = ((color >> 16) & 255) / 255f;
        g = ((color >> 8) & 255) / 255f;
        b = (color & 255) / 255f;
        a = ((color >> 24) & 255) / 255f;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_LINE, true, true, false, false);
        builder.pos(x1, y1, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        int x2 = x + width, y2 = y + height;
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_LINE_LOOP, true, true, false, false);
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y2, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        int x2 = x + width, y2 = y + height;
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_QUADS, true, true, false, false);
        builder.pos(x, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y, 0).color(r, g, b, a).endVertex();
        builder.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        builder.pos(x, y2, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

    }

    @Override
    public void drawText(CharSequence text, int x, int y) {
        fontRenderer.drawText(text, x, y, color);
    }

    @Override
    public void drawTexture(GLTexture texture, int x, int y, int width, int height) {

    }

    public void clipRect(int x, int y, int width, int height) {

    }
}
