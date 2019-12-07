package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.*;
import nullengine.client.rendering.gl.vertex.GLVertexElements;
import nullengine.client.rendering.gl.vertex.GLVertexFormats;
import nullengine.client.rendering.scene.Renderable;
import nullengine.util.Color;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Box implements Renderable {

    private Vector3fc from;
    private Vector3fc to;
    private Color color;

    private VertexArrayObject mesh;

    public Box(Vector3fc from, Vector3fc to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        refreshMesh();
    }

    public void refreshMesh() {
        var min = new Vector3f();
        var max = new Vector3f();
        from.min(to,min);
        from.max(to,max);
        DirectRenderer instance = DirectRenderer.getInstance();
        GLBuffer buffer = instance.getBuffer();
        buffer.begin(GLDrawMode.TRIANGLES, GLVertexFormats.POSITION);
        buffer.pos(min).endVertex();
        buffer.pos(min.x,min.y,max.z).endVertex();
        buffer.pos(min.x,max.y,min.z).endVertex();
        buffer.pos(min.x,max.y,max.z).endVertex();
        buffer.pos(max.x,min.y,min.z).endVertex();
        buffer.pos(max.x,min.y,max.z).endVertex();
        buffer.pos(max.x,max.y,min.z).endVertex();
        buffer.pos(max).endVertex();
        buffer.finish();
        var indices = BufferUtils.createIntBuffer(36);
        indices.put(new int[]{
                0,1,2,
                2,1,3,
                1,3,7,
                7,5,1,
                1,5,0,
                0,5,4,
                4,5,6,
                6,5,7,
                3,2,6,
                6,7,3,
                2,0,4,
                4,6,2
        });
        indices.flip();
        mesh = VertexArrayObject.builder().drawMode(GLDrawMode.TRIANGLES)
                .newBufferAttribute(GLVertexElements.POSITION, GLBufferUsage.STATIC_DRAW, buffer.getBackingBuffer())
                .newIndicesBuffer(GLBufferUsage.STATIC_DRAW, GLDataType.UNSIGNED_INT, indices)
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
