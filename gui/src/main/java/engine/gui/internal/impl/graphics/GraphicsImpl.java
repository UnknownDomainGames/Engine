package engine.gui.internal.impl.graphics;

import engine.graphics.font.TextMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.gui.Node;
import engine.gui.graphics.Graphics;
import engine.gui.image.Image;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.shape.Path2D;
import engine.math.Math2;
import engine.util.Color;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public final class GraphicsImpl implements Graphics {

    private final VertexDataBuf buffer = VertexDataBuf.create(4096);
    private final GUIResourceFactory resourceFactory = new GUIResourceFactory();

    private final Stack<Vector4fc> clipRect = new Stack<>();

    private final Matrix4fc identityMatrix4f = new Matrix4f();

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

    private static class States implements UniformBlock.Value {
        Matrix4fc projMatrix;
        Matrix4fc modelMatrix;
        Vector4fc clipRect;
        boolean renderText;
        boolean enableGamma;

        @Override
        public ByteBuffer get(MemoryStack stack) {
            return get(stack.malloc(152));
        }

        @Override
        public ByteBuffer get(int index, ByteBuffer buffer) {
            projMatrix.get(0, buffer);
            modelMatrix.get(64, buffer);
            clipRect.get(128, buffer);
            buffer.putInt(144, renderText ? 1 : 0);
            buffer.putInt(148, enableGamma ? 1 : 0);
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
        states.projMatrix = new Matrix4f().setOrtho2D(0, frameWidth, frameHeight, 0).scale(scaleX, scaleY, 1);
        setModelMatrix(identityMatrix4f);
        resetClipRect();
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
            setColor(background.getColor());
            fillRect(x, y, width, height);
        }
    }

    @Override
    public void drawMesh(Mesh mesh, Texture2D texture, Matrix4fc modelMatrix) {
        uniformTexture.set(texture);
        setModelMatrix(modelMatrix);
        resource.refresh();
        renderer.drawMesh(mesh);
        setModelMatrix(identityMatrix4f);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    @Override
    public void drawStreamedMesh(DrawMode drawMode, VertexDataBuf mesh, Texture2D texture, Matrix4fc modelMatrix) {
        uniformTexture.set(texture);
        setModelMatrix(modelMatrix);
        resource.refresh();
        renderer.drawStreamed(drawMode, mesh);
        setModelMatrix(identityMatrix4f);
        uniformTexture.set(whiteTexture);
        resource.refresh();
    }

    public void resetClipRect() {
        clipRect.clear();
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
        states.clipRect = clipRect.peek();
        resource.refresh();
        Vector4fc scissor = clipRect.stream().reduce((parent, child) -> {
            var newX = Math2.clamp(child.x(), parent.x(), parent.z());
            var newY = Math2.clamp(child.y(), parent.y(), parent.w());
            var newZ = Math2.clamp(child.z(), parent.x(), parent.z());
            var newW = Math2.clamp(child.w(), parent.y(), parent.w());
            return new Vector4f(newX, newY, newZ, newW);
        }).get();
        float height = scissor.w() - scissor.y();
        renderer.setScissor((int) (scissor.x() * scaleX), (int) (frameHeight - (scissor.y() + height) * scaleY),
                (int) Math.ceil((scissor.z() - scissor.x()) * scaleX), (int) Math.ceil(height * scaleY));
    }

    private void setModelMatrix(Matrix4fc modelMatrix) {
        states.modelMatrix = modelMatrix;
    }

    @Override
    public void enableGamma() {
        states.enableGamma = true;
    }

    @Override
    public void disableGamma() {
        states.enableGamma = false;
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
}
