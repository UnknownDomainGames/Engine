package nullengine.client.rendering.gl;

import nullengine.client.rendering.util.DrawMode;

public class DirectRenderer {

    private static final DirectRenderer INSTANCE = new DirectRenderer(0x100000);
    private GLBuffer buffer;
    private SingleBufferVAO vao;

    private DirectRenderer(int bufferSize) {
        vao = new SingleBufferVAO(GLBufferUsage.DYNAMIC_DRAW, GLDrawMode.TRIANGLES);
        buffer = GLBuffer.createDirectBuffer(bufferSize);
    }

    public static DirectRenderer getInstance() {
        return INSTANCE;
    }

    public GLBuffer getBuffer() {
        return buffer;
    }

    public void draw(DrawMode drawMode) {
        buffer.finish();
        vao.uploadData(buffer);
        vao.setVertexFormat(buffer.getVertexFormat());
        vao.setDrawMode(drawMode);
        vao.draw();
    }
}
