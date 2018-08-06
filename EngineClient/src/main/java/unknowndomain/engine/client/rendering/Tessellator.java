package unknowndomain.engine.client.rendering;

import unknowndomain.engine.client.shader.Shader;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import unknowndomain.engine.client.util.BufferBuilder;
import unknowndomain.engine.client.util.OpenGLHelper;
import unknowndomain.engine.client.util.VertexBufferObject;

import java.nio.ByteBuffer;

public class Tessellator {

    private BufferBuilder buffer;
    private VertexBufferObject vbo;

    private int vertexStatusBufId;
    private static final Tessellator INSTANCE = new Tessellator(1048576);

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public Tessellator(int bufferSize) {
        vbo = new VertexBufferObject();
        buffer = new BufferBuilder(bufferSize);

        vertexStatusBufId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, 4 * 4, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }

    public void draw() {
//        buffer.finish();
//
//        int status = GL31.glGetUniformBlockIndex(OpenGLHelper.getCurrentShaderId(), "VertexStatus");
//        GL31.glUniformBlockBinding(OpenGLHelper.getCurrentShaderId(), status, 0);
//        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 2, vertexStatusBufId);
//
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, vertexStatusBufId);
//        ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) (buffer.isPosEnabled() ? 1 : 0), (byte) (buffer.isColorEnabled() ? 1 : 0), (byte) (buffer.isTexEnabled() ? 1 : 0), (byte) (buffer.isNormalEnabled() ? 1 : 0)});
//        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, bb);
//        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
//        vbo.bind();
//        vbo.uploadData(buffer);
//        vbo.bind();
//
//        int posarr;
//        if (buffer.isPosEnabled()) {
//            Shader.pointVertexAttribute(0, 3, buffer.getOffset(), 0);
//            Shader.enableVertexAttrib(0);
//        }
//        if (buffer.isTexEnabled()) {
//            Shader.pointVertexAttribute(2, 2, buffer.getOffset(), (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
//            Shader.enableVertexAttrib(2);
//        }
//        if (buffer.isColorEnabled()) {
//            Shader.pointVertexAttribute(1, 4, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0)) * Float.BYTES);
//            Shader.enableVertexAttrib(1);
//        }
//        if (buffer.isNormalEnabled()) {
//            Shader.pointVertexAttribute(3, 3, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0) + (buffer.isColorEnabled() ? 4 : 0)) * Float.BYTES);
//            Shader.enableVertexAttrib(3);
//        }
//        vbo.unbind();
//        vbo.bindVAO();
//
//        if (buffer.isUsingIndex()) {
//            vbo.drawElements(buffer.getDrawMode());
//        } else {
//            vbo.drawArrays(buffer.getDrawMode());
//        }
//        vbo.unbind();
//        buffer.reset();
    }
}
