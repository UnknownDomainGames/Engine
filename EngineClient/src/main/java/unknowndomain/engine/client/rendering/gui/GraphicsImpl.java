package unknowndomain.engine.client.rendering.gui;

import org.joml.Vector4f;
import org.joml.Vector4fc;
import unknowndomain.engine.client.gui.rendering.Graphics;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.gui.font.NativeTTFont;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;
import unknowndomain.engine.math.Math2;
import unknowndomain.engine.util.Color;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

public class GraphicsImpl implements Graphics {

    private final RenderContext context;
    private final Tessellator tessellator = Tessellator.getInstance();
    private final GuiRenderer guiRenderer;

    private Color color;
    private Font font;
    private NativeTTFont nativeTTFont;

    private final Stack<Vector4fc> clipRect = new Stack<>();

    public GraphicsImpl(RenderContext context, GuiRenderer guiRenderer) {
        this.context = context;
        this.guiRenderer = guiRenderer;
        setColor(Color.WHITE);
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
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
        this.nativeTTFont = guiRenderer.getFontHelper().getNativeFont(font);
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.LINES, GLBufferFormats.POSITION_COLOR_ALPHA);
        line(buffer, x1, y1, x2, y2);
        tessellator.draw();
    }

    @Override
    public void drawRect(float x, float y, float width, float height) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.LINES_CLOSED, GLBufferFormats.POSITION_COLOR_ALPHA);
        rect(buffer, x, y, width, height);
        tessellator.draw();
    }

    @Override
    public void fillRect(float x, float y, float width, float height) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA);
        rect2(buffer, x, y, width, height);
        tessellator.draw();
    }

    @Override
    public void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        float x2 = x + width, y2 = y + height;
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.LINES_CLOSED, GLBufferFormats.POSITION_COLOR_ALPHA);
        pointTo(buffer, x2 - arcWidth, y);
        quadTo(buffer, x2 - arcWidth, y, x2, y + arcHeight, x2, y);
        pointTo(buffer, x2, y2 - arcHeight);
        quadTo(buffer, x2, y2 - arcHeight, x2 - arcWidth, y2, x2, y2);
        pointTo(buffer, x + arcWidth, y2);
        quadTo(buffer, x + arcWidth, y2, x, y2 - arcHeight, x, y2);
        pointTo(buffer, x, y + arcHeight);
        quadTo(buffer, x, y + arcHeight, x + arcWidth, y, x, y);
        tessellator.draw();
    }

    @Override
    public void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
//        TODO: refine this method to a version that doesn't use GL_POLYGON which is not supported in GL3 onwards
//        float x2 = x + width, y2 = y + height;
//        GLBuffer buffer = tessellator.getBuffer();
//        buffer.begin(GL_POLYGON, true, true, false, false);
//        pointTo(buffer, x2 - arcWidth, y);
//        quadTo(buffer, x2 - arcWidth, y, x2, y + arcHeight, x2, y);
//        pointTo(buffer, x2, y2 - arcHeight);
//        quadTo(buffer, x2, y2 - arcHeight, x2 - arcWidth, y2, x2, y2);
//        pointTo(buffer, x + arcWidth, y2);
//        quadTo(buffer, x + arcWidth, y2, x, y2 - arcHeight, x, y2);
//        pointTo(buffer, x, y + arcHeight);
//        quadTo(buffer, x, y + arcHeight, x + arcWidth, y, x, y);
//        tessellator.draw();
    }

    @Override
    public void drawQuad(float startX, float startY, float endX, float endY, float px, float py) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.CONTINUOUS_LINES, GLBufferFormats.POSITION_COLOR_ALPHA);
        pointTo(buffer, startX, startY);
        quadTo(buffer, startX, startY, endX, endY, px, py);
        tessellator.draw();
    }

    @Override
    public void drawCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.CONTINUOUS_LINES, GLBufferFormats.POSITION_COLOR_ALPHA);
        pointTo(buffer, startX, startY);
        curveTo(buffer, startX, startY, endX, endY, px1, py1, px2, py2);
        tessellator.draw();
    }

    @Override
    public void drawArc(float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag) {
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.CONTINUOUS_LINES, GLBufferFormats.POSITION_COLOR_ALPHA);
        pointTo(buffer, startX, startY);
        arcTo(buffer, startX, startY, endX, endY, radiusX, radiusY, xAxisRotation, largeArcFlag, sweepFlag);
        tessellator.draw();
    }

    @Override
    public void drawText(CharSequence text, float x, float y) {
        guiRenderer.getFontHelper().renderText(text, x, y, color.toRGBA(), nativeTTFont);
    }

    @Override
    public void drawTexture(GLTexture texture, float x, float y, float width, float height) {
        drawTexture(texture, x, y, width, height, TextureUV.DEFAULT_TEXTURE_UV);
    }

    @Override
    public void drawTexture(GLTexture texture, float x, float y, float width, float height, TextureUV textureUV) {
        context.getTextureManager().getWhiteTexture().bind();
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_TEXTURE);
        float x2 = x + width, y2 = y + height;
        buffer.pos(x, y, 0).uv(textureUV.getMinU(), textureUV.getMinV()).endVertex();
        buffer.pos(x, y2, 0).uv(textureUV.getMinU(), textureUV.getMaxV()).endVertex();
        buffer.pos(x2, y, 0).uv(textureUV.getMaxU(), textureUV.getMinV()).endVertex();
        buffer.pos(x2, y2, 0).uv(textureUV.getMaxU(), textureUV.getMaxV()).endVertex();
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        tessellator.draw();
        glDisable(GL_TEXTURE_2D);
    }

    @Override
    public void pushClipRect(float x, float y, float width, float height) {
        if (clipRect.isEmpty()) {
            clipRect.push(new Vector4f(x, y, x + width, y + height));
            updateClipRect();
        } else {
            Vector4fc parent = clipRect.peek();
            float newX = parent.x() + x, newY = parent.y() + y;
            float newZ = newX + width, newW = newY + height;
//            unrestricted range
            Math2.clamp(newX, parent.x(),parent.z());
            Math2.clamp(newY, parent.y(),parent.w());
            Math2.clamp(newZ, parent.x(),parent.z());
            Math2.clamp(newW, parent.y(),parent.w());
//            inclusiveBetween(parent.x(), parent.z(), newX);
//            inclusiveBetween(parent.y(), parent.w(), newY);
//            inclusiveBetween(parent.x(), parent.z(), newZ);
//            inclusiveBetween(parent.y(), parent.w(), newW);
            clipRect.push(new Vector4f(newX, newY, newZ, newW));
            updateClipRect();
        }
    }

    @Override
    public void popClipRect() {
        clipRect.pop();
        updateClipRect();
    }

    private void updateClipRect() {
        if (!clipRect.isEmpty()) {
            guiRenderer.setClipRect(clipRect.peek());
        }
    }

    private void pointTo(GLBuffer buffer, float x, float y) {
        buffer.pos(x, y, 0).color(color).endVertex();
    }

    private void line(GLBuffer buffer, float x1, float y1, float x2, float y2) {
        pointTo(buffer, x1, y1);
        pointTo(buffer, x2, y2);
    }

    private void rect(GLBuffer buffer, float x, float y, float width, float height) {
        float x2 = x + width, y2 = y + height;
        pointTo(buffer, x, y);
        pointTo(buffer, x2, y);
        pointTo(buffer, x2, y2);
        pointTo(buffer, x, y2);
    }
    private void rect2(GLBuffer buffer, float x, float y, float width, float height) {
        float x2 = x + width, y2 = y + height;
        pointTo(buffer, x, y);
        pointTo(buffer, x, y2);
        pointTo(buffer, x2, y);
        pointTo(buffer, x2, y2);
    }

    private void quadTo(GLBuffer buffer, float startX, float startY, float endX, float endY, float px, float py) {
        float step = 12f / (Math.abs(startX - px) + Math.abs(px - endX) + Math.abs(startY - py) + Math.abs(py - endY)); //TODO: optimization
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            pointTo(buffer, startX * f2 * f2 + px * f2 * f * 2 + endX * f * f, startY * f2 * f2 + py * f2 * f * 2 + endY * f * f);
        }
        pointTo(buffer, endX, endY);
    }

    private void curveTo(GLBuffer buffer, float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        float step = 36f / (Math.abs(startX - px1) + Math.abs(px1 - px2) + Math.abs(px2 - endX) + Math.abs(startY - py1) + Math.abs(py1 - py2) + Math.abs(py2 - endY)); //TODO: optimization
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            pointTo(buffer, startX * f2 * f2 * f2 + px1 * f2 * f2 * f * 3 + px2 * f2 * f * f * 3 + endX * f * f * f,
                    startY * f2 * f2 * f2 + py1 * f2 * f2 * f * 3 + py2 * f2 * f * f * 3 + endY * f * f * f);
        }
        pointTo(buffer, endX, endY);
    }

    // https://stackoverflow.com/questions/43946153/approximating-svg-elliptical-arc-in-canvas-with-javascript
    private void arcTo(GLBuffer buffer, float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag) {
        float phi = (float) Math.toRadians(xAxisRotation);
        float rX = Math.abs(radiusX);
        float rY = Math.abs(radiusY);

        float dx2 = (startX - endX) / 2;
        float dy2 = (startY - endY) / 2;

        float x1p = (float) (Math.cos(phi) * dx2 + Math.sin(phi) * dy2);
        float y1p = (float) (-Math.sin(phi) * dx2 + Math.cos(phi) * dy2);

        float rxs = rX * rX;
        float rys = rY * rY;
        float x1ps = x1p * x1p;
        float y1ps = y1p * y1p;

        float cr = x1ps / rxs + y1ps / rys;
        if (cr > 1) {
            float s = (float) Math.sqrt(cr);
            rX = s * rX;
            rY = s * rY;
            rxs = rX * rX;
            rys = rY * rY;
        }

        float dq = (rxs * y1ps + rys * x1ps);
        float pq = (rxs * rys - dq) / dq;
        float q = (float) Math.sqrt(Math.max(0, pq));
        if (largeArcFlag == sweepFlag)
            q = -q;
        float cxp = q * rX * y1p / rY;
        float cyp = -q * rY * x1p / rX;

        float cx = (float) (Math.cos(phi) * cxp - Math.sin(phi) * cyp + (startX + endX) / 2);
        float cy = (float) (Math.sin(phi) * cxp + Math.cos(phi) * cyp + (startY + endY) / 2);

        float theta = angle(1, 0, (x1p - cxp) / rX, (y1p - cyp) / rY);

        float delta = angle(
                (x1p - cxp) / rX, (y1p - cyp) / rY,
                (-x1p - cxp) / rX, (-y1p - cyp) / rY);

        delta = (float) (delta - Math.PI * 2 * Math.floor(delta / (Math.PI * 2)));

        if (!sweepFlag)
            delta -= 2 * Math.PI;

        float n1 = theta,
                n2 = delta;

        // E(n)
        // cx +acosθcosη−bsinθsinη
        // cy +asinθcosη+bcosθsinη

        float[] en1 = E(n1, cx, cy, radiusX, radiusY, phi);
        float[] en2 = E(n2, cx, cy, radiusX, radiusY, phi);
        float[] edn1 = Ed(n1, radiusX, radiusY, phi);
        float[] edn2 = Ed(n2, radiusX, radiusY, phi);

        float alpha = (float) (Math.sin(n2 - n1) * (Math.sqrt(4 + 3 * Math.pow(Math.tan((n2 - n1) / 2), 2)) - 1) / 3);

        curveTo(buffer, startX, startY, endX, endY, en1[0] + alpha * edn1[0], en1[1] + alpha * edn1[1], en2[0] - alpha * edn2[0], en2[1] - alpha * edn2[1]);
    }

    private float[] E(float n, float cx, float cy, float rx, float ry, float phi) {
        float enx = (float) (cx + rx * Math.cos(phi) * Math.cos(n) - ry * Math.sin(phi) * Math.sin(n));
        float eny = (float) (cy + rx * Math.sin(phi) * Math.cos(n) + ry * Math.cos(phi) * Math.sin(n));
        return new float[]{enx, eny};
    }

    // E'(n)
    // −acosθsinη−bsinθcosη
    // −asinθsinη+bcosθcosη
    private float[] Ed(float n, float rx, float ry, float phi) {
        float ednx = (float) (-1 * rx * Math.cos(phi) * Math.sin(n) - ry * Math.sin(phi) * Math.cos(n));
        float edny = (float) (-1 * rx * Math.sin(phi) * Math.sin(n) + ry * Math.cos(phi) * Math.cos(n));
        return new float[]{ednx, edny};
    }

    private float angle(float ux, float uy, float vx, float vy) {
        float dot = ux * vx + uy * vy;
        double len = Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy);

        double angle = Math.acos(Math.min(Math.max(dot / len, -1), 1));
        if ((ux * vy - uy * vx) < 0)
            angle = -angle;
        return (float) angle;
    }
}
