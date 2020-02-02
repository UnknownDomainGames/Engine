package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.buffer.GLBufferType;
import nullengine.client.rendering.gl.buffer.GLBufferUsage;
import nullengine.client.rendering.gl.buffer.GLVertexBuffer;
import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.gl.util.GLHelper;
import nullengine.client.rendering.util.Cleaner;
import nullengine.client.rendering.util.DataType;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class GLVertexArray {

    private int id;
    private Cleaner.Disposable disposable;

    private int vertexCount;

    private VertexAttribute[] attributes;
    private boolean applyBeforeDraw;

    private GLVertexBuffer indexBuffer;
    private DataType indexType;
    private GLDrawMode drawMode;

    private GLVertexArray(int id) {
        this.id = id;
        this.disposable = GLCleaner.registerVertexArray(this, id);
    }

    public VertexAttribute[] getAttributes() {
        return attributes;
    }

    public VertexAttribute getAttribute(int index) {
        return attributes[index];
    }

    public int getAttributeCount() {
        return attributes.length;
    }

    public GLVertexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public DataType getIndexType() {
        return indexType;
    }

    public DrawMode getDrawMode() {
        return drawMode.peer;
    }

    public GLDrawMode getGLDrawMode() {
        return drawMode;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void refreshAttribute() {
        bind();
        applyBeforeDraw = false;
        int index = 0;
        for (VertexAttribute attribute : attributes) {
            if (attribute.type.isApplyBeforeRendering()) {
                applyBeforeDraw = true;
            } else {
                attribute.apply(index);
            }
            index += attribute.getFormat().getElementCount();
        }
        if (indexBuffer != null) {
            indexBuffer.bind();
        }
    }

    public void prepareDraw() {
        if (!applyBeforeDraw) return;
        int index = 0;
        for (VertexAttribute attribute : attributes) {
            if (attribute.type.isApplyBeforeRendering()) {
                attribute.apply(index);
            }
            index += attribute.getFormat().getElementCount();
        }
    }

    public void bind() {
        if (id == 0) {
            throw new IllegalStateException("Object has been disposed");
        }
        GL30.glBindVertexArray(id);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void drawArrays() {
        GL11.glDrawArrays(drawMode.gl, 0, vertexCount);
    }

    public void drawElements() {
        GL30.glDrawElements(drawMode.gl, vertexCount, GLHelper.toGLDataType(indexType), 0);
    }

    public void draw() {
        bind();
        prepareDraw();
        if (indexBuffer == null) {
            drawArrays();
        } else {
            drawElements();
        }
    }

    public void dispose() {
        if (id != 0) {
            for (VertexAttribute attribute : attributes) {
                attribute.dispose();
            }
            disposable.dispose();
            id = 0;
        }
    }

    public static final class VertexAttribute {
        private VertexFormat format;
        private Object value;
        private VertexAttributeType type;

        public VertexAttribute(VertexFormat format, Object value) {
            this.format = format;
            setValue(value);
        }

        public VertexFormat getFormat() {
            return format;
        }

        public Object getValue() {
            return value;
        }

        public GLVertexBuffer getValueAsVertexBuffer() {
            return (GLVertexBuffer) value;
        }

        public void setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;

            if (oldValue != null && oldValue.getClass() == value.getClass()) return;

            for (var type : VertexAttributeType.values()) {
                if (!type.is(value.getClass())) continue;
                this.type = type;
                return;
            }
        }

        public void apply(int index) {
            if (value != null) {
                type.apply(index, format, value);
            } else {
                type.applyDefault(index, format);
            }
        }

        public void dispose() {
            if (value instanceof GLVertexBuffer) {
                ((GLVertexBuffer) value).dispose();
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<VertexAttribute> attributes = new ArrayList<>();

        private GLVertexBuffer indexBuffer;
        private DataType indexType;
        private GLDrawMode drawMode = GLDrawMode.TRIANGLES;
        private GLBufferUsage usage = GLBufferUsage.STATIC_DRAW;

        private int vertexCount;

        private Builder() {
        }

        public Builder setStatic() {
            usage = GLBufferUsage.STATIC_DRAW;
            return this;
        }

        public Builder setDynamic() {
            usage = GLBufferUsage.DYNAMIC_DRAW;
            return this;
        }

        public Builder setStreamed() {
            usage = GLBufferUsage.STREAM_DRAW;
            return this;
        }

        public Builder newBufferAttribute(VertexFormat format) {
            attributes.add(new VertexAttribute(format, new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage)));
            return this;
        }

        public Builder newBufferAttribute(VertexFormat format, ByteBuffer buffer) {
            attributes.add(new VertexAttribute(format, new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage, buffer)));
            if (format.isUsingPosition() && indexBuffer == null) {
                vertexCount = buffer.limit() / format.getBytes();
            }
            return this;
        }

        public Builder newValueAttribute(VertexFormat format, Object value) {
            attributes.add(new VertexAttribute(format, value));
            return this;
        }

        public Builder newIndexBuffer(DataType indexType, ByteBuffer buffer) {
            indexBuffer = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage, buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() / indexType.getBytes();
            return this;
        }

        public Builder newIndexBuffer(DataType indexType, ShortBuffer buffer) {
            indexBuffer = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            indexBuffer.uploadData(buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() * Short.BYTES / indexType.getBytes();
            return this;
        }

        public Builder newIndexBuffer(DataType indexType, IntBuffer buffer) {
            indexBuffer = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            indexBuffer.uploadData(buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() * Integer.BYTES / indexType.getBytes();
            return this;
        }

        public Builder drawMode(DrawMode drawMode) {
            this.drawMode = GLDrawMode.valueOf(drawMode);
            return this;
        }

        public GLVertexArray build() {
            GLVertexArray vao = new GLVertexArray(glGenVertexArrays());
            vao.attributes = attributes.toArray(VertexAttribute[]::new);
            vao.drawMode = drawMode;
            vao.indexBuffer = indexBuffer;
            vao.indexType = indexType;
            vao.vertexCount = vertexCount;
            vao.refreshAttribute();
            return vao;
        }
    }
}
