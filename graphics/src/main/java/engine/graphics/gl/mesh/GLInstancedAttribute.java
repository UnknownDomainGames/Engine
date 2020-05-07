package engine.graphics.gl.mesh;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.mesh.InstancedAttribute;
import engine.graphics.util.StructDefinition;
import engine.graphics.vertex.VertexFormat;
import engine.util.IdentityStrategy;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

final class GLInstancedAttribute<E> implements InstancedAttribute<E> {
    private final VertexFormat format;
    private final StructDefinition<E> structDefinition;
    private final int sizeof;

    private final GLVertexBuffer buffer;
    private final Object2IntMap<E> elementToIndex;
    private final List<E> elements;

    private final ByteBuffer nativeBuffer;

    public GLInstancedAttribute(VertexFormat format, StructDefinition<E> structDefinition, GLBufferUsage usage) {
        this.format = format;
        this.structDefinition = structDefinition;
        this.sizeof = structDefinition.sizeof();
        this.buffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage);
        this.elementToIndex = new Object2IntOpenCustomHashMap<>(IdentityStrategy.instance());
        this.elementToIndex.defaultReturnValue(-1);
        this.elements = new ArrayList<>();
        this.nativeBuffer = ByteBuffer.allocateDirect(structDefinition.sizeof());
    }

    @Override
    public VertexFormat getFormat() {
        return format;
    }

    public GLVertexBuffer getBuffer() {
        return buffer;
    }

    @Override
    public StructDefinition<E> getStructDefinition() {
        return structDefinition;
    }

    @Override
    public int size() {
        return elementToIndex.size();
    }

    @Override
    public boolean add(E e) {
        int index = elementToIndex.size();
        if (elementToIndex.putIfAbsent(e, index) != -1) return false;
        elements.add(index, e);
        uploadElement(index, e);
        return true;
    }

    @Override
    public boolean update(E e) {
        int index = elementToIndex.getInt(e);
        if (index == -1) return false;
        uploadElement(index, e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = elementToIndex.removeInt(o);
        if (index == -1) return false;
        int last = elements.size() - 1;
        if (index != last) {
            E element = elements.get(last);
            elementToIndex.put(element, index);
            elements.set(index, element);
            uploadElement(index, element);
        }
        elements.remove(last);
        return true;
    }

    private void uploadElement(int index, E element) {
        buffer.uploadSubData(index * sizeof, structDefinition.toBytes(nativeBuffer, element));
    }

    @Override
    public void clear() {
        elementToIndex.clear();
        elements.clear();
    }

    @Override
    public boolean contains(Object o) {
        return elementToIndex.containsKey(o);
    }
}
