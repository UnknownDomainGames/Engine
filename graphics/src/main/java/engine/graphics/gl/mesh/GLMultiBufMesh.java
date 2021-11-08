package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.mesh.Attribute;
import engine.graphics.mesh.Indices;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import org.lwjgl.opengl.GL11C;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static engine.graphics.gl.mesh.GLVertexArrayHelper.bindElementBuffer;
import static engine.graphics.gl.mesh.GLVertexArrayHelper.enableVertexFormat;

public class GLMultiBufMesh extends GLMesh implements MultiBufMesh {

    protected List<Attribute> attributes;
    protected GLIndices indices;

    public static Builder builder() {
        return new Builder();
    }

    public void draw(int start, int count) {
        bind();
        if (isDrawIndexed()) {
            GL11C.glDrawElements(drawMode.gl, count, indices.getGLType(), start);
        } else {
            GL11C.glDrawArrays(drawMode.gl, start, count);
        }
    }

    protected final boolean isDrawIndexed() {
        return indices != null;
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public Attribute getAttribute(VertexFormat format) {
        for (Attribute attribute : attributes) {
            if (attribute.getFormat().equals(format)) {
                return attribute;
            }
        }
        return null;
    }

    @Override
    public Indices getIndices() {
        return indices;
    }

    @Override
    public void update() {
        if (isDrawIndexed()) {
            vertexCount = indices.getElementCount();
        } else {
            vertexCount = 0;
            for (Attribute attribute : attributes) {
                int componentCount = attribute.getComponentCount();
                if (vertexCount < componentCount) {
                    vertexCount = componentCount;
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        attributes.forEach(attribute -> ((GLAttribute) attribute).getBuffer().dispose());
        if (indices != null) indices.getBuffer().dispose();
        disposable.dispose();
        id = 0;
    }

    public static class Builder implements MultiBufMesh.Builder {

        private final List<GLAttribute> attributes = new ArrayList<>();
        private GLIndices indices;

        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private DrawMode drawMode = DrawMode.TRIANGLES;

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
            attributes.add(new GLAttribute(format, buffer, bufferUsage));
            return this;
        }

        @Override
        public Builder indices(ByteBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public Builder indices(ShortBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public Builder indices(IntBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public MultiBufMesh build() {
            GLMultiBufMesh mesh = new GLMultiBufMesh();
            mesh.drawMode = GLDrawMode.valueOf(drawMode);
            int index = 0;
            for (GLAttribute attribute : attributes) {
                enableVertexFormat(mesh.id, attribute.getBuffer(), attribute.getFormat(), index);
                index += attribute.getFormat().getIndexCount();
            }
            mesh.attributes = List.copyOf(attributes);
            if (indices != null) {
                mesh.indices = indices;
                bindElementBuffer(mesh.id, indices.getBuffer());
            }
            mesh.update();
            return mesh;
        }
    }
}
