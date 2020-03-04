package engine.graphics.gl;

import engine.graphics.gl.graph.GLRenderer;
import engine.graphics.gl.mesh.GLSingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;

@Deprecated
public final class GLStreamedRenderer {

    private static final GLStreamedRenderer INSTANCE = new GLStreamedRenderer(0x100000);

    private VertexDataBuf buffer;
    private GLSingleBufMesh mesh;

    private GLStreamedRenderer(int initialBufferCapacity) {
        mesh = GLSingleBufMesh.builder().setStreamed().build();
        buffer = VertexDataBuf.create(initialBufferCapacity);
    }

    public static GLStreamedRenderer getInstance() {
        return INSTANCE;
    }

    public VertexDataBuf getBuffer() {
        return buffer;
    }

    public void draw(DrawMode drawMode) {
        if (!buffer.isReady()) {
            buffer.finish();
        }
        mesh.uploadData(buffer);
        mesh.setDrawMode(drawMode);
        GLRenderer.getInstance().drawMesh(mesh);
    }
}
