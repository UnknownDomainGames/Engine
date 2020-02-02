package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.buffer.GLBufferUsage;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;

public class DirectRenderer {

    private static final DirectRenderer INSTANCE = new DirectRenderer(0x100000);
    private VertexDataBuf buffer;
    private SingleBufferVAO vao;

    private DirectRenderer(int bufferSize) {
        vao = new SingleBufferVAO(GLBufferUsage.DYNAMIC_DRAW, GLDrawMode.TRIANGLES);
        buffer = VertexDataBuf.create(bufferSize);
    }

    public static DirectRenderer getInstance() {
        return INSTANCE;
    }

    public VertexDataBuf getBuffer() {
        return buffer;
    }

    public void draw(DrawMode drawMode) {
        if (!buffer.isReady()) {
            buffer.finish();
        }
        vao.uploadData(buffer);
        vao.setDrawMode(drawMode);
        vao.draw();
    }
}
