package nullengine.client.rendering3d.queue;

import nullengine.client.rendering3d.Geometry;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.noNullElements;
import static org.apache.commons.lang3.Validate.notNull;

public class GeometryList implements Iterable<Geometry> {

    private List<Geometry> geometries = new ArrayList<>();

    public int size() {
        return geometries.size();
    }

    public boolean contains(Object o) {
        return geometries.contains(o);
    }

    public boolean add(@Nonnull Geometry geometry) {
        notNull(geometry);
        return geometries.add(geometry);
    }

    public boolean remove(Object o) {
        return geometries.remove(o);
    }

    public boolean addAll(@Nonnull Collection<? extends Geometry> c) {
        noNullElements(c);
        return geometries.addAll(c);
    }

    public void clear() {
        geometries.clear();
    }

    public void sort(Comparator<? super Geometry> c) {
        geometries.sort(c);
    }

    @Override
    public Spliterator<Geometry> spliterator() {
        return geometries.spliterator();
    }

    public Stream<Geometry> stream() {
        return geometries.stream();
    }

    @Override
    public Iterator<Geometry> iterator() {
        return geometries.iterator();
    }
}
