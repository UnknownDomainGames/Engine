package engine.util;

import it.unimi.dsi.fastutil.Hash;

public final class IdentityStrategy<K> implements Hash.Strategy<K> {
    private static final IdentityStrategy<?> INSTANCE = new IdentityStrategy<>();

    @SuppressWarnings("unchecked")
    public static <K> IdentityStrategy<K> instance() {
        return (IdentityStrategy<K>) INSTANCE;
    }

    private IdentityStrategy() {
    }

    @Override
    public int hashCode(K o) {
        return System.identityHashCode(o);
    }

    @Override
    public boolean equals(K a, K b) {
        return a == b;
    }
}
