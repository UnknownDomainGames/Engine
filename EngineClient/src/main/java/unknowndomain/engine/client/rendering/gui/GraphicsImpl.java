package unknowndomain.engine.client.rendering.gui;

import unknowndomain.engine.client.gui.Graphics;
import unknowndomain.engine.client.rendering.gui.font.FontRenderer;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.util.BufferBuilder;
import unknowndomain.engine.client.util.Color;

import static org.lwjgl.opengl.GL11.*;

public class GraphicsImpl implements Graphics {

    private final Tessellator tessellator = Tessellator.getInstance();
    private final FontRenderer fontRenderer;

    private Color color;

    public GraphicsImpl(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        setColor(Color.WRITE);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2) {
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_LINE, true, true, false, false);
        line(builder, x1, y1, x2, y2);
        tessellator.draw();
    }

    @Override
    public void drawRect(float x, float y, float width, float height) {
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_LINE_LOOP, true, true, false, false);
        rect(builder, x, y, width, height);
        tessellator.draw();
    }

    @Override
    public void fillRect(float x, float y, float width, float height) {
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_QUADS, true, true, false, false);
        rect(builder, x, y, width, height);
        tessellator.draw();
    }

    @Override
    public void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {

    }

    @Override
    public void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {

    }

    @Override
    public void drawQuadraticBelzierCurve(float startX, float startY, float endX, float endY, float px, float py) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINE_STRIP, true, true, false, false);
        quadraticBelzierCurve(buffer, startX, startY, endX, endY, px, py);
        tessellator.draw();
    }

    @Override
    public void drawBelzierCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINE_STRIP, true, true, false, false);
        belzierCurve(buffer, startX, startY, endX, endY, px1, py1, px2, py2);
        tessellator.draw();
    }

    @Override
    public void drawEllipticalArc(float startX, float startY, float endX, float endY, float rx, float ry, float xRotation, boolean largeArc, boolean sweep) {

    }

    @Override
    public void drawText(CharSequence text, float x, float y) {
        fontRenderer.drawText(text, x, y, color.toRGBA());
    }

    @Override
    public void drawTexture(GLTexture texture, float x, float y, float width, float height, float u, float v) {

    }

    public void clipRect(int x, int y, int width, int height) {

    }

    private void line(BufferBuilder buffer, float x1, float y1, float x2, float y2) {
        buffer.pos(x1, y1, 0).color(color).endVertex();
        buffer.pos(x2, y2, 0).color(color).endVertex();
    }

    private void rect(BufferBuilder buffer, float x, float y, float width, float height) {
        float x2 = x + width, y2 = y + height;
        buffer.pos(x, y, 0).color(color).endVertex();
        buffer.pos(x2, y, 0).color(color).endVertex();
        buffer.pos(x2, y2, 0).color(color).endVertex();
        buffer.pos(x, y2, 0).color(color).endVertex();
    }

    private void quadraticBelzierCurve(BufferBuilder buffer, float startX, float startY, float endX, float endY, float px, float py) {
        float step = 24f / (Math.abs(startX - px) + Math.abs(px - endX) + Math.abs(startY - py) + Math.abs(py - endY)); //TODO: optimization
        buffer.pos(startX, startY, 0).color(color).endVertex();
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            buffer.pos(startX * f2 * f2 + px * f2 * f * 2 + endX * f * f, startY * f2 * f2 + py * f2 * f * 2 + endY * f * f, 0).color(color).endVertex();
        }
        buffer.pos(endX, endY, 0).color(color).endVertex();
    }

    private void belzierCurve(BufferBuilder buffer, float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        float step = 36f / (Math.abs(startX - px1) + Math.abs(px1 - px2) + Math.abs(px2 - endX) + Math.abs(startY - py1) + Math.abs(py1 - py2) + Math.abs(py2 - endY)); //TODO: optimization
        buffer.pos(startX, startY, 0).color(color).endVertex();
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            buffer.pos(startX * f2 * f2 * f2 + px1 * f2 * f2 * f * 3 + px2 * f2 * f * f * 3 + endX * f * f * f,
                    startY * f2 * f2 * f2 + py1 * f2 * f2 * f * 3 + py2 * f2 * f * f * 3 + endY * f * f * f, 0).color(color).endVertex();
        }
        buffer.pos(endX, endY, 0).color(color).endVertex();
    }

    private void ellipticalArc(BufferBuilder buffer, float startX, float startY, float endX, float endY, float rx, float ry, float angle, boolean largeArc, boolean sweep) {
        angle = largeArc ? (angle <= 180 ? 360 - angle : angle) : (angle <= 180 ? angle : 360 - angle);
        float halfRadians = (float) Math.toRadians(angle / 2);
        boolean flag = Math.asin((startX - endX) / (2 * rx * Math.sin(halfRadians))) == Math.acos((endY - startY) / (2 * ry * Math.sin(halfRadians)));
        System.out.println(flag);

        double rotation1 = Math.asin((startX - endX) / (2 * rx * Math.sin(halfRadians))) - halfRadians;
        double rotation2 = Math.acos((endY - startY) / (2 * ry * Math.sin(halfRadians))) - halfRadians;
        if (Math.abs(rotation1 - rotation2) > 1.0e-6){
            throw new IllegalArgumentException("");
        }
        double oX = startX - rx * Math.cos(rotation1);
        double oY = startY - ry * Math.sin(rotation1);
    }
}
