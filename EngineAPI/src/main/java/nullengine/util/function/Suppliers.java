package nullengine.util.function;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class Suppliers {

    public static <T> Supplier<T> ofWeakReference(T value) {
        WeakReference<T> reference = new WeakReference<>(value);
        return reference::get;
    }
}
