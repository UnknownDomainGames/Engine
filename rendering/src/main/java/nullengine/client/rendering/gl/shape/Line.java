package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.gl.GLSingleBufferMesh;
import nullengine.client.rendering.scene.Renderable;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.util.Color;
import org.joml.Vector3fc;

public class Line implements Renderable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private GLSingleBufferMesh mesh;

    public Line(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        DirectRenderer instance = DirectRenderer.getInstance();
        VertexDataBuf buffer = instance.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA);
        buffer.pos(from).color(color).endVertex();
        buffer.pos(to).color(color).endVertex();
        buffer.finish();
        mesh = GLSingleBufferMesh.builder().drawMode(DrawMode.LINES).build(buffer);
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
