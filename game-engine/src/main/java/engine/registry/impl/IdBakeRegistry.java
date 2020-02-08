package engine.registry.impl;

import engine.registry.Registrable;

import java.util.ArrayList;
import java.util.List;

public class IdBakeRegistry<T extends Registrable<T>> extends SimpleRegistry<T> {

    protected List<T> registeredObjects = new ArrayList<>();
    private volatile boolean baked = false;

    public IdBakeRegistry(Class<T> entryType) {
        super(entryType);
    }

    public IdBakeRegistry(Class<T> entryType, String name) {
        super(entryType, name);
    }

    public synchronized void bake(IdGetter<T> idGetter) {
        baked = true;
        for (T object : registeredObjects) {
            setId(object, idGetter.getId(object));
        }
        registeredObjects = null;
    }

    @FunctionalInterface
    public static interface IdGetter<T> {
        int getId(T obj);
    }
}
