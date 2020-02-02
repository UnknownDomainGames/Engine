package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.buffer.GLBufferType;
import nullengine.client.rendering.gl.buffer.GLBufferUsage;
import nullengine.client.rendering.gl.buffer.GLVertexBuffer;
import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.gl.util.GLHelper;
import nullengine.client.rendering.util.Cleaner;
import nullengine.client.rendering.util.DataType;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexElement;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArrayObject {

    private int id;
    private Cleaner.Disposable disposable;

    private int vertexCount;

    private VertexAttribute[] attributes;
    private boolean applyBeforeRendering;

    private GLVertexBuffer indices;
    private DataType indexType;
    private GLDrawMode drawMode;

    private VertexArrayObject(int id) {
        this.id = id;
        this.disposable = GLCleaner.registerVertexArray(this, id);
    }

    public VertexAttribute getAttribute(int index) {
        return attributes[index];
    }

    public int getAttributeLength() {
        return attributes.length;
    }

    public GLVertexBuffer getIndices() {
        return indices;
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

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public void refreshAttribute() {
        bind();
        applyBeforeRendering = false;
        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];
            if (attribute.type.isApplyBeforeRendering())
                applyBeforeRendering = true;
            else
                attribute.apply(i);
        }
        if (indices != null) {
            indices.bind();
        }
    }

    public void prepareRender() {
        if (!applyBeforeRendering) return;
        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];
            if (attribute.type.isApplyBeforeRendering()) {
                attribute.apply(i);
            }
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
        prepareRender();
        if (indices == null) {
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
        private VertexElement element;
        private Object value;
        private VertexAttributeType type;

        public VertexAttribute(VertexElement element, Object value) {
            this.element = element;
            setValue(value);
        }

        public VertexElement getElement() {
            return element;
        }

        public Object getValue() {
            return value;
        }

        public GLVertexBuffer getValueAsVBO() {
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
                type.apply(index, element, value);
            } else {
                type.applyDefault(index, element);
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

        private GLVertexBuffer indices;
        private DataType indexType;
        private DrawMode drawMode = DrawMode.TRIANGLES;

        private int vertexCount;

        private Builder() {
        }

        public Builder newBufferAttribute(VertexElement element, GLBufferUsage usage) {
            attributes.add(new VertexAttribute(element, new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage)));
            return this;
        }

        public Builder newBufferAttribute(VertexElement element, GLBufferUsage usage, ByteBuffer buffer) {
            attributes.add(new VertexAttribute(element, new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage, buffer)));
            if (element.getName().equals(VertexElement.NAME_POSITION) && indices == null) {
                vertexCount = buffer.limit() / element.getBytes();
            }
            return this;
        }

        public Builder newValueAttribute(VertexElement element, Object value) {
            attributes.add(new VertexAttribute(element, value));
            return this;
        }

        public Builder newIndicesBuffer(GLBufferUsage usage, DataType indexType) {
            indices = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            this.indexType = indexType;
            return this;
        }

        public Builder newIndicesBuffer(GLBufferUsage usage, DataType indexType, ByteBuffer buffer) {
            indices = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage, buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() / indexType.getBytes();
            return this;
        }

        public Builder newIndicesBuffer(GLBufferUsage usage, DataType indexType, ShortBuffer buffer) {
            indices = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            indices.uploadData(buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() * Short.BYTES / indexType.getBytes();
            return this;
        }

        public Builder newIndicesBuffer(GLBufferUsage usage, DataType indexType, IntBuffer buffer) {
            indices = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            indices.uploadData(buffer);
            this.indexType = indexType;
            this.vertexCount = buffer.limit() * Integer.BYTES / indexType.getBytes();
            return this;
        }

        public Builder drawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        public VertexArrayObject build() {
            VertexArrayObject vao = new VertexArrayObject(glGenVertexArrays());
            vao.attributes = attributes.toArray(VertexAttribute[]::new);
            vao.drawMode = GLDrawMode.valueOf(notNull(drawMode, "Draw mode cannot be null"));
            vao.indices = indices;
            vao.indexType = indexType;
            vao.vertexCount = vertexCount;
            vao.refreshAttribute();
            return vao;
        }
    }
}
