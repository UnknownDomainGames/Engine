package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.mesh.InstancedAttribute;
import engine.graphics.mesh.InstancedMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.util.StructDefinition;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;
import org.lwjgl.opengl.GL31C;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static engine.graphics.gl.mesh.GLVertexArrayHelper.bindElementBuffer;
import static engine.graphics.gl.mesh.GLVertexArrayHelper.enableVertexFormat;

public final class GLInstancedMesh<E> extends GLMultiBufMesh implements InstancedMesh<E> {

    private GLInstancedAttribute<E> instancedAttribute;

    @Override
    public InstancedAttribute<E> getInstancedAttribute() {
        return instancedAttribute;
    }

    public void draw(int start, int count) {
        bind();
        if (isDrawIndexed()) {
            GL31C.glDrawElementsInstanced(drawMode.gl, count, indices.getGLType(), start, instancedAttribute.size());
        } else {
            GL31C.glDrawArraysInstanced(drawMode.gl, start, count, instancedAttribute.size());
        }
    }

    public final static class Builder<E> implements InstancedMesh.Builder<E> {

        private final List<GLAttribute> attributes = new ArrayList<>();
        private GLIndices indices;
        private GLInstancedAttribute<E> instancedAttribute;

        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private DrawMode drawMode = DrawMode.TRIANGLES;

        @Override
        public Builder<E> setStatic() {
            bufferUsage = GLBufferUsage.STATIC_DRAW;
            return this;
        }

        @Override
        public Builder<E> setDynamic() {
            bufferUsage = GLBufferUsage.DYNAMIC_DRAW;
            return this;
        }

        @Override
        public Builder<E> setStreamed() {
            bufferUsage = GLBufferUsage.STREAM_DRAW;
            return this;
        }

        @Override
        public Builder<E> drawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        @Override
        public Builder<E> attribute(VertexDataBuffer buffer) {
            return attribute(buffer.getVertexFormat(), buffer.getByteBuffer());
        }

        @Override
        public Builder<E> attribute(VertexFormat format, ByteBuffer buffer) {
            attributes.add(new GLAttribute(format, buffer, bufferUsage));
            return this;
        }

        @Override
        public Builder<E> indices(ByteBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public Builder<E> indices(ShortBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public Builder<E> indices(IntBuffer buffer) {
            indices = new GLIndices(buffer, bufferUsage);
            return this;
        }

        @Override
        public Builder<E> instancedAttribute(VertexFormat format, StructDefinition<E> structDefinition) {
            instancedAttribute = new GLInstancedAttribute<>(format, structDefinition, bufferUsage);
            return this;
        }

        @Override
        public GLInstancedMesh<E> build() {
            GLInstancedMesh<E> mesh = new GLInstancedMesh<>();
            mesh.drawMode = GLDrawMode.valueOf(drawMode);
            int index = 0;
            for (GLAttribute attribute : attributes) {
                enableVertexFormat(mesh.id, attribute.getBuffer(), attribute.getFormat(), index);
                index += attribute.getFormat().getIndexCount();
            }
            mesh.attributes = List.copyOf(attributes);
            mesh.instancedAttribute = instancedAttribute;
            enableVertexFormat(mesh.id, instancedAttribute.getBuffer(), instancedAttribute.getFormat(), index);
            if (indices != null) {
                mesh.indices = indices;
                bindElementBuffer(mesh.id, indices.getBuffer());
            }
            mesh.update();
            return mesh;
        }
    }
}
