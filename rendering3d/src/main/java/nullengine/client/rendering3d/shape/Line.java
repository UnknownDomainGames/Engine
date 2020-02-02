package nullengine.client.rendering3d.shape;

import nullengine.client.rendering.mesh.SingleBufferMesh;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.client.rendering3d.Renderable;
import nullengine.util.Color;
import org.joml.Vector3fc;

public class Line implements Renderable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private SingleBufferMesh mesh;

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
        mesh = SingleBufferMesh.builder().drawMode(DrawMode.LINES).build(buffer);
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
