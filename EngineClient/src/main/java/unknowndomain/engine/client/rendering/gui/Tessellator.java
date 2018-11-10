package unknowndomain.engine.client.rendering.gui;

import unknowndomain.engine.client.rendering.shader.Shader;
import unknowndomain.engine.client.util.BufferBuilder;
import unknowndomain.engine.client.util.VertexBufferObject;

public class Tessellator {
    private static final Tessellator INSTANCE = new Tessellator(1048576);
    private BufferBuilder buffer;
    private VertexBufferObject vbo;
    private int shaderId;
    private int vertexStatusBufId;

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

    public void setShaderId(int shaderId) {
        this.shaderId = shaderId;

//        int status = GL31.glGetUniformBlockIndex(programId, "VertexStatus");
//        GL31.glUniformBlockBinding(programId, status, 0);
//        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 2, vertexStatusBufId);
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
        vbo.bind();
        vbo.uploadData(buffer);
        vbo.bind();

        if (buffer.isPosEnabled()) {
            Shader.pointVertexAttribute(0, 3, buffer.getOffset(), 0);
            Shader.enableVertexAttrib(0);
        }
        if (buffer.isColorEnabled()) {
            Shader.pointVertexAttribute(1, 4, buffer.getOffset(), (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
            Shader.enableVertexAttrib(1);
        }
        if (buffer.isTexEnabled()) {
            Shader.pointVertexAttribute(2, 2, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isColorEnabled() ? 4 : 0)) * Float.BYTES);
            Shader.enableVertexAttrib(2);
        }
        if (buffer.isNormalEnabled()) {
            Shader.pointVertexAttribute(3, 3, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0) + (buffer.isColorEnabled() ? 4 : 0)) * Float.BYTES);
            Shader.enableVertexAttrib(3);
        }
        vbo.unbind();
        vbo.bindVAO();

        if (buffer.isUsingIndex()) {
            vbo.drawElements(buffer.getDrawMode());
        } else {
            vbo.drawArrays(buffer.getDrawMode());
        }
        vbo.unbind();
        buffer.reset();
    }
}
