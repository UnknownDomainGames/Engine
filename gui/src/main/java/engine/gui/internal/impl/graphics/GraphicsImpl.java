package engine.gui.internal.impl.graphics;

import engine.graphics.font.TextMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.gui.Node;
import engine.gui.image.Image;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.rendering.Graphics;
import engine.util.Color;
import org.joml.*;

import java.lang.Math;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public final class GraphicsImpl implements Graphics {

    private final VertexDataBuf buffer = VertexDataBuf.create(4096);
    private final GUIResourceFactory resourceFactory = new GUIResourceFactory();

    private final Stack<Vector4fc> clipRect = new Stack<>();
    private final Stack<Matrix4fc> modelMatrix = new Stack<>();

    private final ShaderResource resource;
    private final UniformTexture uniformTexture;

    private final Texture2D whiteTexture;

    private Renderer renderer;
    private int frameWidth;
    private int frameHeight;
    private float scaleX;
    private float scaleY;

    private Color color;

    public GraphicsImpl(ShaderResource resource) {
        this.resource = resource;
        this.uniformTexture = resource.getUniformTexture("u_Texture");
        this.whiteTexture = Texture2D.white();
        setColor(Color.WHITE);
    }

    public void setup(Renderer renderer, int frameWidth, int frameHeight, float scaleX, float scaleY) {
        this.renderer = renderer;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        resource.setUniform("u_ProjMatrix", new Matrix4f().setOrtho2D(0, frameWidth, frameHeight, 0));
        pushModelMatrix(new Matrix4f().scale(scaleX, scaleY, 1));
        pushClipRect(0, 0, frameWidth / scaleX, frameHeight / scaleY);
        this.uniformTexture.set(whiteTexture);
        this.resource.refresh();
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
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x1, y1);
        putVertex(buffer, x2, y2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINES, buffer);
    }

    @Override
    public void drawRect(float x, float y, float width, float height) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        float x2 = x + width, y2 = y + height;
        quads(buffer, x, y, x2, y, x2, y2, x, y2);
        putVertex(buffer, x, y);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void fillRect(float x, float y, float width, float height) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        float x2 = x + width, y2 = y + height;
        quads(buffer, x, y, x, y2, x2, y, x2, y2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_STRIP, buffer);
    }

    @Override
    public void drawQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        quads(buffer, p1.x(), p1.y(), p2.x(), p2.y(), p3.x(), p3.y(), p4.x(), p4.y());
        putVertex(buffer, p1.x(), p1.y());
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void fillQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        quads(buffer, p1.x(), p1.y(), p4.x(), p4.y(), p2.x(), p2.y(), p3.x(), p3.y());
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_STRIP, buffer);
    }

    @Override
    public void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        float x2 = x + width, y2 = y + height;
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        quadCurveTo(buffer, x2 - arcWidth, y, x2, y + arcHeight, x2, y);
        quadCurveTo(buffer, x2, y2 - arcHeight, x2 - arcWidth, y2, x2, y2);
        quadCurveTo(buffer, x + arcWidth, y2, x, y2 - arcHeight, x, y2);
        quadCurveTo(buffer, x, y + arcHeight, x + arcWidth, y, x, y);
        putVertex(buffer, x2 - arcWidth, y);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        float x2 = x + width, y2 = y + height;

        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x + arcWidth, y + arcHeight);
        quadCurveTo(buffer, x + arcWidth, y, x, y + arcHeight, x, y);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_FAN, buffer);

        fillRect(x, y + arcHeight, arcWidth, height - arcHeight * 2);

        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x + arcWidth, y2 - arcHeight);
        quadCurveTo(buffer, x, y2 - arcHeight, x + arcWidth, y2, x, y2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_FAN, buffer);

        fillRect(x + arcWidth, y2 - arcHeight, width - arcWidth * 2, arcHeight);

        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x2 - arcWidth, y2 - arcHeight);
        quadCurveTo(buffer, x2 - arcWidth, y2, x2, y2 - arcHeight, x2, y2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_FAN, buffer);

        fillRect(x2 - arcWidth, y + arcHeight, arcWidth, height - arcHeight * 2);

        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x2 - arcWidth, y + arcHeight);
        quadCurveTo(buffer, x2, y + arcHeight, x2 - arcWidth, y, x2, y);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_FAN, buffer);

        fillRect(x + arcWidth, y, width - arcWidth * 2, height - arcHeight);
    }

    @Override
    public void drawCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, startX, startY);
        curveTo(buffer, startX, startY, endX, endY, px1, py1, px2, py2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void drawQuadCurve(float startX, float startY, float endX, float endY, float px, float py) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        quadCurveTo(buffer, startX, startY, endX, endY, px, py);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void drawArc(float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, startX, startY);
        arcTo(buffer, startX, startY, endX, endY, radiusX, radiusY, xAxisRotation, largeArcFlag, sweepFlag);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void drawText(TextMesh mesh, float x, float y) {
        drawText(mesh, 0, mesh.length(), x, y);
    }

    @Override
    public void drawText(TextMesh mesh, int beginIndex, int endIndex, float x, float y) {
        setRenderText(true);
        uniformTexture.set(mesh.getTexture());
        resource.refresh();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD);
        buffer.setTranslation(x, y, 0);
        mesh.put(buffer, color, beginIndex, endIndex);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLES, buffer);
        setRenderText(false);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    private void setRenderText(boolean renderText) {
        resource.setUniform("u_RenderText", renderText);
    }

    @Override
    public void drawTexture(Texture2D texture, float x, float y, float width, float height) {
        drawTexture(texture, x, y, width, height, 0f, 0f, 1f, 1f);
    }

    @Override
    public void drawTexture(Texture2D texture, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD);
        float x2 = x + width, y2 = y + height;
        buffer.pos(x, y, 0).rgba(1, 1, 1, 1).tex(minU, minV).endVertex();
        buffer.pos(x, y2, 0).rgba(1, 1, 1, 1).tex(minU, maxV).endVertex();
        buffer.pos(x2, y, 0).rgba(1, 1, 1, 1).tex(maxU, minV).endVertex();
        buffer.pos(x2, y2, 0).rgba(1, 1, 1, 1).tex(maxU, maxV).endVertex();
        uniformTexture.set(texture);
        resource.refresh();
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_STRIP, buffer);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    @Override
    public void drawImage(Image image, float x, float y, float width, float height) {
        Texture2D texture = resourceFactory.getTexture(image);
        if (texture == null) {
            return;
        }
        drawTexture(texture, x, y, width, height);
    }

    @Override
    public void drawImage(Image image, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV) {
        Texture2D texture = resourceFactory.getTexture(image);
        if (texture == null) {
            return;
        }
        drawTexture(texture, x, y, width, height, minU, minV, maxU, maxV);
    }

    @Override
    public void drawBorder(Border border, Node node) {
        drawBorder(border, 0, 0, node.width().get(), node.height().get());
    }

    @Override
    public void drawBorder(Border border, float x, float y, float width, float height) {
        if (border == null) {
            return;
        }

        setColor(border.getColor());
        float top = border.getInsets().getTop();
        if (top != 0) {
            fillRect(x, y, width, top);
        }
        float bottom = border.getInsets().getBottom();
        if (bottom != 0) {
            fillRect(x, y + height - bottom, width, bottom);
        }
        float left = border.getInsets().getLeft();
        if (left != 0) {
            fillRect(x, y, left, height);
        }
        float right = border.getInsets().getRight();
        if (right != 0) {
            fillRect(x + width - right, y, right, height);
        }
    }

    @Override
    public void drawBackground(Background background, Node node) {
        drawBackground(background, 0, 0, node.width().get(), node.height().get());
    }

    @Override
    public void drawBackground(Background background, float x, float y, float width, float height) {
        if (background == null) {
            return;
        }

        Image image = background.getImage();
        if (image != null) {
            Texture2D texture = resourceFactory.getTexture(image);
            if (texture == null) return;

            if (background.isRepeat()) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            } else {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }

            drawTexture(texture, x, y, width, height);
        } else {
            setColor(background.getColor());
            fillRect(x, y, width, height);
        }
    }

    @Override
    public void pushClipRect(float x, float y, float width, float height) {
        if (clipRect.isEmpty()) {
            clipRect.push(new Vector4f(x, y, x + width, y + height));
        } else {
            Vector4fc parent = clipRect.peek();
            float newX = parent.x() + x, newY = parent.y() + y;
            float newZ = newX + width, newW = newY + height;
            clipRect.push(new Vector4f(newX, newY, Math.min(newZ, parent.z()), Math.min(newW, parent.w())));
        }
        updateClipRect();
    }

    @Override
    public void popClipRect() {
        clipRect.pop();
        updateClipRect();
    }

    private void updateClipRect() {
        Vector4fc peek = clipRect.peek();
        resource.setUniform("u_ClipRect", peek);
        float height = peek.w() - peek.y();
        renderer.setScissor(Math.round(peek.x()), frameHeight - Math.round(peek.y() + height),
                Math.round(peek.z() - peek.x()), Math.round(height));
    }

    @Override
    public void pushModelMatrix(Matrix4fc matrix) {
        if (modelMatrix.isEmpty()) {
            modelMatrix.push(matrix);
        } else {
            modelMatrix.push(modelMatrix.peek().mul(matrix, new Matrix4f()));
        }
        updateModelMatrix();
    }

    @Override
    public void popModelMatrix() {
        modelMatrix.pop();
        updateModelMatrix();
    }

    private void updateModelMatrix() {
        resource.setUniform("u_ModelMatrix", modelMatrix.peek());
    }

    @Override
    public void enableGamma() {
        resource.setUniform("u_EnableGamma", true);
    }

    @Override
    public void disableGamma() {
        resource.setUniform("u_EnableGamma", false);
    }

    private void putVertex(VertexDataBuf buffer, float x, float y) {
        buffer.pos(x, y, 0).color(color).endVertex();
    }

    private void quads(VertexDataBuf buffer, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        putVertex(buffer, x0, y0);
        putVertex(buffer, x1, y1);
        putVertex(buffer, x2, y2);
        putVertex(buffer, x3, y3);
    }

    private void quadCurveTo(VertexDataBuf buffer, float startX, float startY, float endX, float endY, float px, float py) {
        float step = 12f / (Math.abs(startX - px) + Math.abs(px - endX) + Math.abs(startY - py) + Math.abs(py - endY)); //TODO: optimization
        putVertex(buffer, startX, startY);
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            putVertex(buffer, startX * f2 * f2 + px * f2 * f * 2 + endX * f * f, startY * f2 * f2 + py * f2 * f * 2 + endY * f * f);
        }
        putVertex(buffer, endX, endY);
    }

    private void curveTo(VertexDataBuf buffer, float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2) {
        float step = 36f / (Math.abs(startX - px1) + Math.abs(px1 - px2) + Math.abs(px2 - endX) + Math.abs(startY - py1) + Math.abs(py1 - py2) + Math.abs(py2 - endY)); //TODO: optimization
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            putVertex(buffer, startX * f2 * f2 * f2 + px1 * f2 * f2 * f * 3 + px2 * f2 * f * f * 3 + endX * f * f * f,
                    startY * f2 * f2 * f2 + py1 * f2 * f2 * f * 3 + py2 * f2 * f * f * 3 + endY * f * f * f);
        }
        putVertex(buffer, endX, endY);
    }

    // https://stackoverflow.com/questions/43946153/approximating-svg-elliptical-arc-in-canvas-with-javascript
    private void arcTo(VertexDataBuf buffer, float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag) {
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
