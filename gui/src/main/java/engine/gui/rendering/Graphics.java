package engine.gui.rendering;

import engine.graphics.font.TextMesh;
import engine.graphics.mesh.Mesh;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.gui.Node;
import engine.gui.image.Image;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.util.Color;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;

public interface Graphics {

    Color getColor();

    void setColor(Color color);

    void drawLine(float x1, float y1, float x2, float y2);

    void drawRect(float x, float y, float width, float height);

    void fillRect(float x, float y, float width, float height);

    void drawQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4);

    void fillQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4);

    void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    /**
     * Draw a quadratic Belzier curve
     */
    void drawQuadCurve(float startX, float startY, float endX, float endY, float px, float py);

    /**
     * Draw a Belazier curve
     */
    void drawCurve(float startX, float startY, float endX, float endY, float px1, float py1, float px2, float py2);

    /**
     * Draw a elliptical arc
     */
    void drawArc(float startX, float startY, float endX, float endY, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag);

    // TODO: void drawPolygon(Polygon polygon);

    // TODO: void fillPolygon(Polygon polygon);

    void drawText(TextMesh mesh, float x, float y);

    void drawText(TextMesh mesh, int beginIndex, int endIndex, float x, float y);

    void drawTexture(Texture2D texture, float x, float y, float width, float height);

    void drawTexture(Texture2D texture, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV);

    void drawImage(Image image, float x, float y, float width, float height);

    void drawImage(Image image, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV);

    void drawBorder(Border border, Node node);

    void drawBorder(Border border, float x, float y, float width, float height);

    void drawBackground(Background background, Node node);

    void drawBackground(Background background, float x, float y, float width, float height);

    void drawMesh(Mesh mesh, Texture2D texture, Matrix4fc modelMatrix);

    void drawStreamedMesh(DrawMode drawMode, VertexDataBuf mesh, Texture2D texture, Matrix4fc modelMatrix);

    void pushClipRect(float x, float y, float width, float height);

    void popClipRect();

    void enableGamma();

    void disableGamma();
}
