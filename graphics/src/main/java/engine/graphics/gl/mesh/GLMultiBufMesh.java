package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.util.DataType;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static engine.graphics.gl.mesh.GLVertexArrayHelper.bindElementBuffer;
import static engine.graphics.gl.mesh.GLVertexArrayHelper.enableVertexFormat;

public class GLMultiBufMesh extends GLMesh implements MultiBufMesh {

    private Attribute[] attributes;
    private MeshIndices indices;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Attribute[] getAttributes() {
        return attributes;
    }

    @Override
    public Attribute getAttribute(VertexFormat format) {
        for (Attribute attribute : attributes) {
            if (attribute.getFormat().equals(format)) return attribute;
        }
        return null;
    }

    @Override
    public Indices getIndices() {
        return indices;
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        for (Attribute attribute : attributes)
            ((MeshAttribute) attribute).buffer.dispose();
        if (indices != null) indices.buffer.dispose();
        disposable.dispose();
        id = 0;
    }

    @Override
    protected boolean hasIndices() {
        return indices != null;
    }

    @Override
    protected int getIndicesType() {
        return indices.glType;
    }

    private static class MeshAttribute implements Attribute {

        private final VertexFormat format;
        private final GLVertexBuffer buffer;

        private GLMultiBufMesh mesh;

        public MeshAttribute(VertexFormat format, ByteBuffer buffer, GLBufferUsage usage) {
            this.format = format;
            this.buffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage);
            if (buffer != null) {
                this.buffer.uploadData(buffer);
            }
        }

        @Override
        public VertexFormat getFormat() {
            return format;
        }

        @Override
        public void uploadData(VertexDataBuf buf) {
            if (!format.equals(buf.getVertexFormat())) throw new IllegalArgumentException();
            uploadData(buf.getByteBuffer());
        }

        @Override
        public void uploadData(ByteBuffer buffer) {
            this.buffer.uploadData(buffer);
            if (format.isUsingPosition()) mesh.vertexCount = buffer.limit() / format.getBytes();
        }
    }

    private static class MeshIndices implements Indices {

        private final GLVertexBuffer buffer;
        private final DataType type;
        private final int glType;

        private GLMultiBufMesh mesh;

        public MeshIndices(Buffer buffer, GLBufferUsage usage) {
            this.buffer = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
            if (buffer instanceof IntBuffer) {
                this.type = DataType.UNSIGNED_INT;
                this.buffer.uploadData((IntBuffer) buffer);
            } else if (buffer instanceof ShortBuffer) {
                this.type = DataType.UNSIGNED_SHORT;
                this.buffer.uploadData((ShortBuffer) buffer);
            } else if (buffer instanceof ByteBuffer) {
                this.type = DataType.UNSIGNED_BYTE;
                this.buffer.uploadData((ByteBuffer) buffer);
            } else {
                throw new IllegalArgumentException();
            }
            this.glType = GLHelper.toGLDataType(this.type);

        }

        @Override
        public DataType getType() {
            return type;
        }

        @Override
        public void uploadData(ByteBuffer buffer) {
            if (type != DataType.UNSIGNED_BYTE) throw new IllegalArgumentException();
            this.buffer.uploadData(buffer);
            mesh.vertexCount = buffer.limit();
        }

        @Override
        public void uploadData(ShortBuffer buffer) {
            if (type != DataType.UNSIGNED_SHORT) throw new IllegalArgumentException();
            this.buffer.uploadData(buffer);
            mesh.vertexCount = buffer.limit();
        }

        @Override
        public void uploadData(IntBuffer buffer) {
            if (type != DataType.UNSIGNED_INT) throw new IllegalArgumentException();
            this.buffer.uploadData(buffer);
            mesh.vertexCount = buffer.limit();
        }
    }

    public static class Builder implements MultiBufMesh.Builder {

        private List<MeshAttribute> attributes = new ArrayList<>();
        private MeshIndices indices;

        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private DrawMode drawMode = DrawMode.TRIANGLES;
        private int vertexCount;

        @Override
        public Builder setStatic() {
            bufferUsage = GLBufferUsage.STATIC_DRAW;
            return this;
        }

        @Override
        public Builder setDynamic() {
            bufferUsage = GLBufferUsage.DYNAMIC_DRAW;
            return this;
        }

        @Override
        public Builder setStreamed() {
            bufferUsage = GLBufferUsage.STREAM_DRAW;
            return this;
        }

        @Override
        public Builder drawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        @Override
        public Builder attribute(VertexDataBuf buf) {
            return attribute(buf.getVertexFormat(), buf.getByteBuffer());
        }

        @Override
        public Builder attribute(VertexFormat format, ByteBuffer buffer) {
            attributes.add(new MeshAttribute(format, buffer, bufferUsage));
            if (format.isUsingPosition() && indices == null) {
                vertexCount = buffer.limit() / format.getBytes();
            }
            return this;
        }

        @Override
        public Builder indices(ByteBuffer buffer) {
            indices = new MeshIndices(buffer, bufferUsage);
            vertexCount = buffer.limit();
            return this;
        }

        @Override
        public Builder indices(ShortBuffer buffer) {
            indices = new MeshIndices(buffer, bufferUsage);
            vertexCount = buffer.limit();
            return this;
        }

        @Override
        public Builder indices(IntBuffer buffer) {
            indices = new MeshIndices(buffer, bufferUsage);
            vertexCount = buffer.limit();
            return this;
        }

        @Override
        public MultiBufMesh build() {
            GLMultiBufMesh mesh = new GLMultiBufMesh();
            mesh.drawMode = GLDrawMode.valueOf(drawMode);
            int index = 0;
            for (MeshAttribute attribute : attributes) {
                attribute.mesh = mesh;
                enableVertexFormat(mesh.id, attribute.buffer, attribute.format, index);
                index += attribute.format.getIndexCount();
            }
            mesh.attributes = attributes.toArray(Attribute[]::new);
            if (indices != null) {
                mesh.indices = indices;
                indices.mesh = mesh;
                bindElementBuffer(mesh.id, indices.buffer);
            }
            mesh.vertexCount = vertexCount;
            return mesh;
        }
    }
}
