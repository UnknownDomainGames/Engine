package nullengine.client.rendering.queue;

import nullengine.client.rendering.scene.Geometry;

import java.util.*;
import java.util.stream.Stream;

public class GeometryList implements Iterable<Geometry> {

    private List<Geometry> geometries = new ArrayList<>();

    public int size() {
        return geometries.size();
    }

    public boolean contains(Object o) {
        return geometries.contains(o);
    }

    public boolean add(Geometry geometry) {
        return geometries.add(geometry);
    }

    public boolean remove(Object o) {
        return geometries.remove(o);
    }

    public boolean addAll(Collection<? extends Geometry> c) {
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
