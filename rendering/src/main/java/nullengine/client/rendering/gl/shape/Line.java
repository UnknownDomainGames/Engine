package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.SingleBufferVAO;
import nullengine.client.rendering.scene.Renderable;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.util.Color;
import org.joml.Vector3fc;

public class Line implements Renderable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private SingleBufferVAO mesh;

    public Line(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        DirectRenderer instance = DirectRenderer.getInstance();
        GLBuffer buffer = instance.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        buffer.pos(from).color(color).endVertex();
        buffer.pos(to).color(color).endVertex();
        buffer.finish();
        mesh = SingleBufferVAO.builder().drawMode(DrawMode.LINES)
                .vertexFormat(VertexFormat.POSITION_COLOR_ALPHA)
                .build(buffer);
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
