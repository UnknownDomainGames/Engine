package engine.registry;

import java.util.Map;

public interface SynchronizableRegistry<T extends Registrable<T>> extends Registry<T> {
    /**
     * Get the id before synchronization
     *
     * @return
     */
    int getIntrinsicId(T obj);

    void sync(Map<String, Integer> map);

    void unsync();
}
