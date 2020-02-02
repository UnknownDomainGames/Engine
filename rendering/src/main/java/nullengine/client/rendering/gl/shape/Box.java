package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.gl.GLBufferUsage;
import nullengine.client.rendering.gl.VertexArrayObject;
import nullengine.client.rendering.scene.Renderable;
import nullengine.client.rendering.util.DataType;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexElement;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.util.Color;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Box implements Renderable {

    private static final byte[] indices = new byte[]{
            // facing +x
            5, 4, 6,
            6, 7, 5,
            // facing -x
            2, 0, 1,
            1, 3, 2,
            // facing +y
            7, 6, 2,
            2, 3, 7,
            // facing -y
            0, 4, 5,
            5, 1, 0,
            // facing +z
            1, 5, 7,
            7, 3, 1,
            // facing -z
            6, 4, 0,
            0, 2, 6
    };

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private VertexArrayObject mesh;

    public Box(Vector3fc center, float size, Color color) {
        this(center, size, size, size, color);
    }

    public Box(Vector3fc center, float xLength, float yLength, float zLength, Color color) {
        this(new Vector3f(center).sub(xLength / 2, yLength / 2, zLength / 2),
                new Vector3f(center).add(xLength / 2, yLength / 2, zLength / 2),
                color);
    }

    public Box(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        var min = new Vector3f();
        var max = new Vector3f();
        from.min(to, min);
        from.max(to, max);
        DirectRenderer instance = DirectRenderer.getInstance();
        VertexDataBuf buffer = instance.getBuffer();
        buffer.begin(VertexFormat.POSITION);
        buffer.pos(min).endVertex();
        buffer.pos(min.x, min.y, max.z).endVertex();
        buffer.pos(min.x, max.y, min.z).endVertex();
        buffer.pos(min.x, max.y, max.z).endVertex();
        buffer.pos(max.x, min.y, min.z).endVertex();
        buffer.pos(max.x, min.y, max.z).endVertex();
        buffer.pos(max.x, max.y, min.z).endVertex();
        buffer.pos(max).endVertex();
        buffer.finish();
        var indicesBuffer = BufferUtils.createByteBuffer(36).put(indices).flip();
        mesh = VertexArrayObject.builder().drawMode(DrawMode.TRIANGLES)
                .newBufferAttribute(VertexElement.POSITION, GLBufferUsage.STATIC_DRAW, buffer.getBuffer())
                .newIndicesBuffer(GLBufferUsage.STATIC_DRAW, DataType.UNSIGNED_BYTE, indicesBuffer)
                .newValueAttribute(VertexElement.COLOR_RGBA, new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()))
                .build();
    }

    @Override
    public void render() {
        mesh.draw();
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
