package engine.util;

import java.util.function.Supplier;

public class LazyObject<T> {
    private T instance;
    private Supplier<T> supplier;

    public LazyObject(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (instance == null) {
            instance = supplier.get();
            supplier = null; //TODO: should we destroy the supplier?
        }
        return instance;
    }
}
