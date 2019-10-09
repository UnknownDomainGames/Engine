package nullengine.client.rendering;

import nullengine.client.rendering.gl.SingleBufferVAO;
import nullengine.client.rendering.gl.buffer.GLBuffer;

public class Tessellator {

    private static final Tessellator INSTANCE = new Tessellator(1048576);
    private GLBuffer buffer;
    private SingleBufferVAO vao;

    private Tessellator(int bufferSize) {
        vao = new SingleBufferVAO();
        buffer = GLBuffer.createDirectBuffer(bufferSize);

//        vertexStatusBufId = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
//        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, 4 * 4, GL15.GL_STATIC_DRAW);
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    }

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public GLBuffer getBuffer() {
        return buffer;
    }

    public void draw() {
        buffer.finish();

//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
//        ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) (buffer.isPosEnabled() ? 1 : 0), (byte) (buffer.isColorEnabled() ? 1 : 0), (byte) (buffer.isTexEnabled() ? 1 : 0), (byte) (buffer.isNormalEnabled() ? 1 : 0)});
//        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, bb);
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        vao.uploadData(buffer);
        vao.bind();
        buffer.getFormat().bind();
        vao.drawArrays(buffer.getDrawMode().gl);
        vao.unbind();
    }
}
