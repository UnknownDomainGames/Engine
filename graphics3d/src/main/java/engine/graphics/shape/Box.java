package engine.graphics.shape;

import engine.graphics.Drawable;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class Box implements Drawable {

    private static final ByteBuffer indices = BufferUtils.createByteBuffer(36).put(new byte[]{
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
    }).flip();

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private MultiBufMesh mesh;

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
        VertexDataBuf buf = VertexDataBuf.currentThreadBuffer();
        buf.begin(VertexFormat.POSITION_COLOR_ALPHA);
        buf.pos(min).color(color).endVertex();
        buf.pos(min.x, min.y, max.z).color(color).endVertex();
        buf.pos(min.x, max.y, min.z).color(color).endVertex();
        buf.pos(min.x, max.y, max.z).color(color).endVertex();
        buf.pos(max.x, min.y, min.z).color(color).endVertex();
        buf.pos(max.x, min.y, max.z).color(color).endVertex();
        buf.pos(max.x, max.y, min.z).color(color).endVertex();
        buf.pos(max).color(color).endVertex();
        buf.finish();
        mesh = MultiBufMesh.builder().attribute(buf).indices(indices).build();
    }

    @Override
    public void draw() {
        mesh.draw();
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
