package engine.graphics.mesh;

import engine.graphics.util.StructDefinition;
import engine.graphics.vertex.VertexFormat;

public interface InstancedAttribute<E> {
    VertexFormat getFormat();

    StructDefinition<E> getStructDefinition();

    int size();

    boolean add(E e);

    boolean update(E e);

    boolean remove(Object o);

    boolean contains(E e);

    void clear();
}
