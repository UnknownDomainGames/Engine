package engine.util;

import java.util.Set;

public interface KeyComparable<K, T extends KeyComparable<K, T>> extends Comparable<T> {

    K key();

    Set<K> beforeThis();

    Set<K> afterThis();

    @Override
    default int compareTo(T o) {
        if (afterThis().contains(o.key()) || o.beforeThis().contains(key()))
            return 1;

        if (beforeThis().contains(o.key()) || o.afterThis().contains(key()))
            return -1;

        return 0;
    }
}
