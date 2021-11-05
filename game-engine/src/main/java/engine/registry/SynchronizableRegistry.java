package engine.registry;

import java.util.Map;

public interface SynchronizableRegistry<T extends Registrable<T>> extends Registry<T> {
    /**
     * Get the id before synchronization
     *
     * @return
     */
    int getIntrinsicId(T obj);

    void sync(String key, int id);

    default void sync(Map<String, Integer> map) {
        if (getEntries().size() != map.size())
            throw new IllegalArgumentException("Sync map size does not match with size of registered objects");
        map.forEach(this::sync);
    }

    void unsync();
}
