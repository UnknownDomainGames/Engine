package engine.graphics.shape;

import engine.graphics.Drawable;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;
import org.joml.Vector3fc;

public class Line implements Drawable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private SingleBufMesh mesh;

    public Line(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        VertexDataBuf buffer = VertexDataBuf.currentThreadBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        buffer.pos(from).color(color).endVertex();
        buffer.pos(to).color(color).endVertex();
        buffer.finish();
        mesh = SingleBufMesh.builder().drawMode(DrawMode.LINES).build(buffer);
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
