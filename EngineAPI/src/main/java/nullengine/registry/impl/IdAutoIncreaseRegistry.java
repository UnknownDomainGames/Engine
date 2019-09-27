package nullengine.registry.impl;

import nullengine.registry.Registrable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

@NotThreadSafe
public class IdAutoIncreaseRegistry<T extends Registrable<T>> extends SimpleRegistry<T> {

    private final AtomicInteger nextId = new AtomicInteger(0);

    public IdAutoIncreaseRegistry(Class entryType) {
        super(entryType);
    }

    public IdAutoIncreaseRegistry(Class entryType, String name) {
        super(entryType, name);
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        super.register(obj);

        setId(obj, nextId.getAndIncrement());

        return obj;
    }
}
