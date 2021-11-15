package engine.gui.internal.impl.graphics;

import engine.graphics.font.TextMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.shape.Path2D;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.util.Struct;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;
import engine.gui.Node;
import engine.gui.graphics.Graphics;
import engine.gui.image.Image;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.primitives.Rectanglei;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.joml.Matrix4fc.PROPERTY_TRANSLATION;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;

public final class GraphicsImpl implements Graphics {

    private final VertexDataBuffer buffer = VertexDataBuffer.create(4096);
    private final GUIResourceFactory resourceFactory = new GUIResourceFactory();

    private final ShaderResource resource;

    private final UniformBlock uniformStates;
    private final States states = new States();

    private final UniformTexture uniformTexture;
    private final Texture2D whiteTexture;

    private Renderer renderer;
    private int frameWidth;
    private int frameHeight;
    private float scaleX;
    private float scaleY;

    private Color color;

    private final Matrix4f transform = new Matrix4f();
    private boolean simpleTransform = false;
    private float transX = 0;
    private float transY = 0;
    private float transZ = 0;

    private final Rectanglei viewport = new Rectanglei();
    private final Rectanglei clipRect = new Rectanglei();
    private final Rectanglei finalClipRect = new Rectanglei();

    private static class States implements Struct {
        final Matrix4f projMatrix = new Matrix4f();
        final Matrix4f modelMatrix = new Matrix4f();
        boolean renderText;
        boolean enableGamma;

        @Override
        public int sizeof() {
            return 136;
        }

        @Override
        public ByteBuffer get(int index, ByteBuffer buffer) {
            projMatrix.get(0, buffer);
            modelMatrix.get(64, buffer);
            buffer.putInt(128, renderText ? 1 : 0);
            buffer.putInt(132, enableGamma ? 1 : 0);
            return buffer;
        }
    }

    public GraphicsImpl(ShaderResource resource) {
        this.resource = resource;
        this.uniformStates = resource.getUniformBlock("States");
        this.uniformStates.set(states);
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
        this.viewport.setMin(0, 0).setMax(frameWidth, frameHeight);
        this.states.projMatrix.setOrtho(0, frameWidth, frameHeight, 0, 32767, -32768);
        this.uniformTexture.set(whiteTexture);
        this.resource.refresh();
        updateTransform();
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
    public Matrix4f getTransformNoClone() {
        return transform;
    }

    @Override
    public Matrix4f getTransform() {
        return new Matrix4f(transform);
    }

    @Override
    public Matrix4f getTransform(Matrix4f dest) {
        return dest.set(transform);
    }

    @Override
    public void setTransform(Matrix4fc m) {
        transform.set(m);
        updateTransform();
    }

    @Override
    public void transform(Matrix4fc m) {
        transform.mul(m);
        updateTransform();
    }

    @Override
    public void translate(float x, float y) {
        transform.translate(x, y, 0);
        updateTransform();
    }

    @Override
    public void translate(float x, float y, float z) {
        transform.translate(x, y, z);
        updateTransform();
    }

    @Override
    public void scale(float x, float y) {
        transform.scale(x, y, 1);
        updateTransform();
    }

    @Override
    public void scale(float x, float y, float z) {
        transform.scale(x, y, z);
        updateTransform();
    }

    private void updateTransform() {
        if ((transform.properties() & PROPERTY_TRANSLATION) != 0) {
            if (!simpleTransform) {
                simpleTransform = true;
                states.modelMatrix.identity();
                resource.refresh();
            }
            transX = transform.m30();
            transY = transform.m31();
            transZ = transform.m32();
            buffer.setTranslation(transX, transY, transZ);
        } else {
            if (simpleTransform) {
                simpleTransform = false;
                transX = transY = transZ = 0;
                buffer.setTranslation(0, 0, 0);
            }
            states.modelMatrix.set(transform);
            resource.refresh();
        }
    }

    @Override
    public Rectanglei getClipRectNoClone() {
        return clipRect;
    }

    @Override
    public Rectanglei getClipRect() {
        return new Rectanglei(clipRect);
    }

    @Override
    public Rectanglei getClipRect(Rectanglei dest) {
        return dest.set(clipRect);
    }

    @Override
    public void setClipRect(Rectanglei dest) {
        finalClipRect.set(viewport);
        if (dest != null) {
            clipRect.set(dest);
            finalClipRect.intersection(clipRect);
            int x = finalClipRect.minX, y = frameHeight - finalClipRect.maxY,
                    width = finalClipRect.lengthX(), height = finalClipRect.lengthY();
            renderer.setScissor(x, y, width, height);
        } else {
            clipRect.set(viewport);
            renderer.setScissor(null);
        }
    }

    @Override
    public void resetClipRect() {
        finalClipRect.set(viewport);
        clipRect.set(viewport);
        renderer.setScissor(null);
    }

    @Override
    public void draw(Path2D path, float x, float y) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        FloatBuffer points = path.getBuffer();
        for (int i = 0, size = points.position(); i < size; i += 2) {
            putVertex(buffer, x + points.get(i), y + points.get(i + 1));
        }
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, this.buffer);
    }

    @Override
    public void fill(Path2D path, float x, float y) {
        if (!path.isClosed()) {
            throw new IllegalStateException("Cannot fill open path");
        }
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        FloatBuffer points = path.getBuffer();
        for (int i = 0, size = points.position() - 2; i < size; i += 2) {
            putVertex(buffer, x + points.get(i), y + points.get(i + 1));
        }
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLE_FAN, buffer);
    }

    @Override
    public void drawQuad(float x1, float y1, float x2, float y2) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x1, y1);
        putVertex(buffer, x1, y2);
        putVertex(buffer, x2, y2);
        putVertex(buffer, x2, y1);
        putVertex(buffer, x1, y1);
        buffer.finish();
        renderer.drawStreamed(DrawMode.LINE_STRIP, buffer);
    }

    @Override
    public void fillQuad(float x1, float y1, float x2, float y2) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, x1, y1);
        putVertex(buffer, x2, y2);
        putVertex(buffer, x2, y1);
        putVertex(buffer, x1, y1);
        putVertex(buffer, x1, y2);
        putVertex(buffer, x2, y2);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLES, buffer);
    }

    @Override
    public void drawRect(float x, float y, float width, float height) {
        drawQuad(x, y, x + width, y + height);
    }

    @Override
    public void fillRect(float x, float y, float width, float height) {
        fillQuad(x, y, x + width, y + height);
    }

    @Override
    public void fillQuad(Vector2fc p1, Vector2fc p2, Vector2fc p3, Vector2fc p4) {
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        putVertex(buffer, p1.x(), p1.y());
        putVertex(buffer, p2.x(), p2.y());
        putVertex(buffer, p3.x(), p3.y());
        putVertex(buffer, p2.x(), p2.y());
        putVertex(buffer, p4.x(), p4.y());
        putVertex(buffer, p3.x(), p3.y());
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLES, buffer);
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
        translate(x, y);
        mesh.put(buffer, color, beginIndex, endIndex);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLES, buffer);
        translate(-x, -y);
        setRenderText(false);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    private void setRenderText(boolean renderText) {
        states.renderText = renderText;
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
        drawBorder(border, 0, 0, node.getWidth(), node.getHeight());
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
        drawBackground(background, 0, 0, node.getWidth(), node.getHeight());
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
            if (background.getColor() == Color.TRANSPARENT) {
                return;
            }
            setColor(background.getColor());
            fillRect(x, y, width, height);
        }
    }

    @Override
    public void drawMesh(Mesh mesh, Texture2D texture) {
        uniformTexture.set(texture);
        resource.refresh();
        renderer.drawMesh(mesh);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    @Override
    public void drawStreamedMesh(DrawMode drawMode, VertexDataBuffer buffer, Texture2D texture) {
        uniformTexture.set(texture);
        resource.refresh();
        renderer.drawStreamed(drawMode, buffer);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    private void putVertex(VertexDataBuffer buffer, float x, float y) {
        buffer.pos(x, y, 0).color(color).endVertex();
    }
}
