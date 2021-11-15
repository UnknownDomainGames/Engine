package engine.graphics.gl.graph;

import engine.graphics.gl.mesh.GLMesh;
import engine.graphics.gl.mesh.GLSingleBufMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11C;

public final class GLRenderer implements Renderer {

    private static final GLRenderer INSTANCE = new GLRenderer();

    private final GLSingleBufMesh streamedMesh;

    public static GLRenderer getInstance() {
        return INSTANCE;
    }

    public GLRenderer() {
        streamedMesh = GLSingleBufMesh.builder().setStreamed().build();
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        setScissor(new Vector4i(x, y, width, height));
    }

    @Override
    public void setScissor(Vector4ic scissor) {
        if (scissor == null) {
            GL11C.glDisable(GL11C.GL_SCISSOR_TEST);
        } else {
            GL11C.glEnable(GL11C.GL_SCISSOR_TEST);
            GL11C.glScissor(scissor.x(), scissor.y(), scissor.z(), scissor.w());
        }
    }

    @Override
    public void drawMesh(Mesh mesh) {
        drawMesh(mesh, 0, mesh.getVertexCount());
    }

    @Override
    public void drawMesh(Mesh mesh, int first, int count) {
        if (count == 0) return;
        ((GLMesh) mesh).draw(first, count);
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer) {
        drawStreamed(drawMode, buffer, 0, buffer.getVertexCount());
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuffer buffer, int first, int count) {
        if (count == 0) return;
        streamedMesh.uploadData(buffer);
        streamedMesh.setDrawMode(drawMode);
        streamedMesh.draw(first, count);
    }
}
