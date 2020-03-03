package engine.graphics.gl.graph;

import engine.graphics.gl.mesh.GLSingleBufferMesh;
import engine.graphics.graph.Renderer;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11;

public final class GLRenderer implements Renderer {

    private static final GLRenderer INSTANCE = new GLRenderer();

    private final GLSingleBufferMesh streamedMesh;

    private Vector4ic scissor;

    public static GLRenderer getInstance() {
        return INSTANCE;
    }

    public GLRenderer() {
        streamedMesh = GLSingleBufferMesh.builder().setStreamed().build();
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        setScissor(new Vector4i(x, y, width, height));
    }

    @Override
    public void setScissor(Vector4ic scissor) {
        this.scissor = scissor;
        if (scissor == null) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissor.x(), scissor.y(), scissor.z(), scissor.w());
        }
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf) {
        if (!buf.isReady()) buf.finish();
        streamedMesh.uploadData(buf);
        streamedMesh.setDrawMode(drawMode);
        streamedMesh.draw();
    }

    @Override
    public void drawStreamed(DrawMode drawMode, VertexDataBuf buf, int first, int count) {
        if (!buf.isReady()) buf.finish();
        streamedMesh.uploadData(buf);
        streamedMesh.setDrawMode(drawMode);
        streamedMesh.draw(first, count);
    }
}
