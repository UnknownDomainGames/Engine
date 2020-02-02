package nullengine.client.rendering.gl.shape;

import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.gl.GLBufferUsage;
import nullengine.client.rendering.gl.VertexArrayObject;
import nullengine.client.rendering.scene.Renderable;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexElement;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Pane implements Renderable {
    private Vector2f from;
    private Vector2f to;

    private Color color;

    private VertexArrayObject mesh;

    public Pane(Vector2f pointA,Vector2f pointB,Color color){
        this.color = color;
        this.from = pointA;
        this.to = pointB;
        refreshMesh();
    }

    private void refreshMesh() {
        var min = new Vector2f();
        var max = new Vector2f();
        from.min(to,min);
        from.max(to,max);
        DirectRenderer instance = DirectRenderer.getInstance();
        VertexDataBuf buffer = instance.getBuffer();
        buffer.begin(VertexFormat.POSITION);
        buffer.pos(min.x, max.y, -10).endVertex();
        buffer.pos(min.x,min.y,-10).endVertex();
        buffer.pos(max.x,min.y,-10).endVertex();
        buffer.pos(max.x,max.y,-10).endVertex();
        buffer.finish();
        mesh = VertexArrayObject.builder().drawMode(DrawMode.TRIANGLE_FAN)
                .newBufferAttribute(VertexElement.POSITION, GLBufferUsage.STATIC_DRAW, buffer.getBackingBuffer())
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
