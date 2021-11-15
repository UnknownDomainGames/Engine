package engine.graphics.mesh;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DrawMode;
import engine.graphics.util.StructDefinition;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface InstancedMesh<E> extends MultiBufMesh {
    static <E> Builder<E> builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createInstancedMeshBuilder();
    }

    InstancedAttribute<E> getInstancedAttribute();

    interface Builder<E> {
        Builder<E> setStatic();

        Builder<E> setDynamic();

        Builder<E> setStreamed();

        Builder<E> drawMode(DrawMode drawMode);

        Builder<E> attribute(VertexDataBuffer buffer);

        Builder<E> attribute(VertexFormat format, ByteBuffer buffer);

        Builder<E> indices(ByteBuffer buffer);

        Builder<E> indices(ShortBuffer buffer);

        Builder<E> indices(IntBuffer buffer);

        Builder<E> instancedAttribute(VertexFormat format, StructDefinition<E> structDefinition);

        InstancedMesh<E> build();
    }
}
