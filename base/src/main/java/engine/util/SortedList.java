package engine.util;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class SortedList<E> extends AbstractList<E> {

    public static <E> SortedList<E> create(Comparator<E> comparator) {
        return create(comparator, LinkedList::new);
    }

    public static <E> SortedList<E> create(Comparator<E> comparator, Supplier<List<E>> constructor) {
        return new SortedList<>(constructor.get(), comparator);
    }

    public static <E extends Comparable<E>> SortedList<E> create() {
        return create(Comparable::compareTo);
    }

    public static <E extends Comparable<E>> SortedList<E> create(Supplier<List<E>> constructor) {
        return create(Comparable::compareTo, constructor);
    }

    public static <E> SortedList<E> copyOf(Collection<E> coll, Comparator<E> comparator) {
        var sortedList = create(comparator);
        for (E e : coll) {
            if (e != null) sortedList.add(e);
        }
        return sortedList;
    }

    public static <E extends Comparable<E>> SortedList<E> copyOf(Collection<E> coll) {
        return copyOf(coll, Comparable::compareTo);
    }

    @SafeVarargs
    public static <E> SortedList<E> of(Comparator<E> comparator, E... elements) {
        var sortedList = create(comparator);
        Collections.addAll(sortedList, elements);
        return sortedList;
    }

    @SafeVarargs
    public static <E extends Comparable<E>> SortedList<E> of(E... elements) {
        return of(Comparable::compareTo, elements);
    }

    private final List<E> list;
    private final Comparator<E> comparator;

    protected SortedList(List<E> list, Comparator<E> comparator) {
        this.list = list;
        this.comparator = comparator;
    }

    @Override
    public boolean add(@Nonnull E e) {
        Objects.requireNonNull(e, "Element cannot be null.");
        int index = 0;
        for (int size = size(); index < size; index++) {
            if (comparator.compare(e, get(index)) < 0) {
                list.add(index, e);
                return true;
            }
        }
        return list.add(e);
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public int size() {
        return list.size();
    }

}
