package engine.gui.graphics;

import engine.graphics.font.TextMesh;
import engine.graphics.mesh.Mesh;
import engine.graphics.shape.Path2D;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.gui.Node;
import engine.gui.image.Image;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.primitives.Rectanglei;

public interface Graphics {

    Color getColor();

    void setColor(Color color);

    Matrix4f getTransformNoClone();

    Matrix4f getTransform();

    Matrix4f getTransform(Matrix4f dest);

    void setTransform(Matrix4fc m);

    void transform(Matrix4fc m);

    void translate(float x, float y);

    void translate(float x, float y, float z);

    void scale(float x, float y);

    void scale(float x, float y, float z);

    Rectanglei getClipRectNoClone();

    Rectanglei getClipRect();

    Rectanglei getClipRect(Rectanglei dest);

    void setClipRect(Rectanglei dest);

    void resetClipRect();

    void draw(Path2D path, float x, float y);

    void fill(Path2D path, float x, float y);

    void drawQuad(float x1, float y1, float x2, float y2);

    void fillQuad(float x1, float y1, float x2, float y2);

    void drawRect(float x, float y, float width, float height);

    void fillRect(float x, float y, float width, float height);

    @Deprecated
    void fillQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4);

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

    void drawMesh(Mesh mesh, Texture2D texture);

    void drawStreamedMesh(DrawMode drawMode, VertexDataBuf mesh, Texture2D texture);
}
