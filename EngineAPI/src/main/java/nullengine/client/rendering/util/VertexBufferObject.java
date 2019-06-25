package nullengine.client.rendering.util;

import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.disposer.Disposable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// TODO: Don't use GL_STATIC_DRAW
public class VertexBufferObject implements Disposable {
    private int vaoId = -1;
    private int id = -1;
    private int eleid = -1;
    private int count;

    public VertexBufferObject() {
        id = GL15.glGenBuffers();
        eleid = GL15.glGenBuffers();
        vaoId = GL30.glGenVertexArrays();
    }

    public void bind() {
        bindVAO();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eleid);
    }

    public void bindVAO() {
        GL30.glBindVertexArray(vaoId);
    }

    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void uploadData(GLBuffer builder) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder.getBackingBuffer(), GL15.GL_STATIC_DRAW);
        unbind();
        this.count = builder.getVertexCount();
    }

    public void uploadData(FloatBuffer builder, int vertex) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder, GL15.GL_STATIC_DRAW);
        unbind();
        this.count = vertex;
    }

    public void uploadSubData(GLBuffer builder) {
        bind();
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, builder.getBackingBuffer());
        unbind();
        count = builder.getVertexCount();
    }

    public void uploadData(ByteBuffer builder, int vertexCount) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder, GL15.GL_STATIC_DRAW);
        unbind();
        this.count = vertexCount;
    }

    public void uploadElementData(IntBuffer builder, int vertex) {
        bind();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, builder, GL15.GL_STATIC_DRAW);
        unbind();
        this.count = vertex;
    }

    private void allocateData() {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ByteBuffer.allocateDirect(1048576), GL15.GL_DYNAMIC_DRAW);
        unbind();
        this.count = 0;
    }

    public void drawArrays(int mode) {
        bind();
        GL11.glDrawArrays(mode, 0, this.count);
        unbind();
    }

    public void drawElements(int mode) {
        bind();
        GL11.glDrawElements(mode, this.count, GL11.GL_UNSIGNED_INT, 0);
        unbind();
    }

    @Override
    public void dispose() {
        if (id != -1) {
            GL15.glDeleteBuffers(id);
            id = -1;
        }
        if (vaoId != -1) {
            GL30.glDeleteVertexArrays(vaoId);
            vaoId = -1;
        }
    }

    @Override
    public boolean isDisposed() {
        return id == -1;
    }
}
