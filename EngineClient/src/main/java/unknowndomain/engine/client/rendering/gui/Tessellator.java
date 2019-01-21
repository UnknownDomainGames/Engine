package unknowndomain.engine.client.rendering.gui;

import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.util.VertexBufferObject;

public class Tessellator {

    private static final Tessellator INSTANCE = new Tessellator(1048576);
    private BufferBuilder buffer;
    private VertexBufferObject vbo;

    private Tessellator(int bufferSize) {
        vbo = new VertexBufferObject();
        buffer = new BufferBuilder(bufferSize);

//        vertexStatusBufId = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
//        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, 4 * 4, GL15.GL_STATIC_DRAW);
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    }

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }

    public void draw() {
        buffer.finish();

//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
//        ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) (buffer.isPosEnabled() ? 1 : 0), (byte) (buffer.isColorEnabled() ? 1 : 0), (byte) (buffer.isTexEnabled() ? 1 : 0), (byte) (buffer.isNormalEnabled() ? 1 : 0)});
//        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, bb);
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        vbo.uploadData(buffer);
        vbo.bind();

        if (buffer.isPosEnabled()) {
            ShaderProgram.pointVertexAttribute(0, 3, buffer.getOffset(), 0);
            ShaderProgram.enableVertexAttrib(0);
        }
        if (buffer.isColorEnabled()) {
            ShaderProgram.pointVertexAttribute(1, 4, buffer.getOffset(), 3 * Float.BYTES);
            ShaderProgram.enableVertexAttrib(1);
        }
        if (buffer.isTexEnabled()) {
            ShaderProgram.pointVertexAttribute(2, 2, buffer.getOffset(), 7 * Float.BYTES);
            ShaderProgram.enableVertexAttrib(2);
        }
        if (buffer.isNormalEnabled()) {
            ShaderProgram.pointVertexAttribute(3, 3, buffer.getOffset(), 9 * Float.BYTES);
            ShaderProgram.enableVertexAttrib(3);
        }

        vbo.drawArrays(buffer.getDrawMode());
        vbo.unbind();
    }
}
