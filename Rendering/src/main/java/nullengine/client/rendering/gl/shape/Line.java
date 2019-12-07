package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.*;
import nullengine.client.rendering.gl.vertex.GLVertexElements;
import nullengine.client.rendering.gl.vertex.GLVertexFormats;
import nullengine.client.rendering.scene.Renderable;
import nullengine.util.Color;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class Line implements Renderable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private VertexArrayObject mesh;

    public Line(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        DirectRenderer instance = DirectRenderer.getInstance();
        GLBuffer buffer = instance.getBuffer();
        buffer.begin(GLDrawMode.LINES, GLVertexFormats.POSITION);
        buffer.pos(from).endVertex();
        buffer.pos(to).endVertex();
        buffer.finish();
        mesh = VertexArrayObject.builder().drawMode(GLDrawMode.LINES)
                .newBufferAttribute(GLVertexElements.POSITION, GLBufferUsage.STATIC_DRAW, buffer.getBackingBuffer())
                .newValueAttribute(GLVertexElements.COLOR_RGBA, new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()))
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
