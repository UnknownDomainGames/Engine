package nullengine.client.rendering.util;

import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.disposer.Disposable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObject implements Disposable {
    public static final int UNALLOCATED_ID = 0;

    private int vaoId = UNALLOCATED_ID;
    private int id = UNALLOCATED_ID;
    private int eleId = UNALLOCATED_ID;
    private Usage usage;

    private int count;

    public enum Usage {
        STREAM_DRAW(0x88E0),
        STREAM_READ(0x88E1),
        STREAM_COPY(0x88E2),
        STATIC_DRAW(0x88E4),
        STATIC_READ(0x88E5),
        STATIC_COPY(0x88E6),
        DYNAMIC_DRAW(0x88E8),
        DYNAMIC_READ(0x88E9),
        DYNAMIC_COPY(0x88EA);

        public final int gl;

        Usage(int gl) {
            this.gl = gl;
        }
    }

    public VertexBufferObject() {
        this(Usage.STATIC_DRAW);
    }

    public VertexBufferObject(Usage usage) {
        id = GL15.glGenBuffers();
        eleId = GL15.glGenBuffers();
        vaoId = GL30.glGenVertexArrays();
        setUsage(usage);
    }

    public void bind() {
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eleId);
    }

    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, UNALLOCATED_ID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, UNALLOCATED_ID);
        GL30.glBindVertexArray(UNALLOCATED_ID);
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public void uploadData(GLBuffer builder) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder.getBackingBuffer(), usage.gl);
        unbind();
        this.count = builder.getVertexCount();
    }

    public void uploadData(FloatBuffer builder, int vertex) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder, usage.gl);
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
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder, usage.gl);
        unbind();
        this.count = vertexCount;
    }

    public void uploadElementData(IntBuffer builder, int vertex) {
        bind();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, builder, usage.gl);
        unbind();
        this.count = vertex;
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
        if (id != UNALLOCATED_ID) {
            GL15.glDeleteBuffers(id);
            id = UNALLOCATED_ID;
        }
        if (vaoId != UNALLOCATED_ID) {
            GL30.glDeleteVertexArrays(vaoId);
            vaoId = UNALLOCATED_ID;
        }
        if (eleId != UNALLOCATED_ID) {
            GL15.glDeleteBuffers(eleId);
            eleId = UNALLOCATED_ID;
        }
    }

    @Override
    public boolean isDisposed() {
        return id == UNALLOCATED_ID;
    }
}
