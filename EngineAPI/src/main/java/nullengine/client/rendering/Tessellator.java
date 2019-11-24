package nullengine.client.rendering;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.GLBufferUsage;
import nullengine.client.rendering.gl.GLDrawMode;
import nullengine.client.rendering.gl.SingleBufferVAO;

public class Tessellator {

    private static final Tessellator INSTANCE = new Tessellator(0x100000);
    private GLBuffer buffer;
    private SingleBufferVAO vao;

    private Tessellator(int bufferSize) {
        vao = new SingleBufferVAO(GLBufferUsage.DYNAMIC_DRAW, GLDrawMode.TRIANGLES);
        buffer = GLBuffer.createDirectBuffer(bufferSize);
    }

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public GLBuffer getBuffer() {
        return buffer;
    }

    public void draw() {
        buffer.finish();
        vao.uploadData(buffer);
        vao.bind();
        buffer.getVertexFormat().applyAndEnable();
        vao.setDrawMode(buffer.getDrawMode());
        vao.drawArrays();
        vao.unbind();
    }
}
