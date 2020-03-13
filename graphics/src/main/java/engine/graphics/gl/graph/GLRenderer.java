package engine.graphics.gl.graph;

import engine.graphics.gl.mesh.GLMesh;
import engine.graphics.gl.mesh.GLSingleBufMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.mesh.Mesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11;

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
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissor.x(), scissor.y(), scissor.z(), scissor.w());
        }
    }

    @Override
    public void drawMesh(Mesh mesh) {
        drawMesh(mesh, 0, mesh.getVertexCount());
    }

    @Override
    public void drawMesh(Mesh mesh, int first, int count) {
        ((GLMesh) mesh).draw(first, count);
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf) {
        drawStreamed(drawMode, buf, 0, buf.getVertexCount());
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf, int first, int count) {
        streamedMesh.uploadData(buf);
        streamedMesh.setDrawMode(drawMode);
        streamedMesh.draw(first, count);
    }
}
