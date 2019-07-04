package nullengine.util;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class Suppliers {

    public static <T> Supplier<T> ofWeakReference(T value) {
        return new Supplier<>() {
            WeakReference<T> reference = new WeakReference<>(value);

            @Override
            public T get() {
                return reference.get();
            }
        };
    }
}
