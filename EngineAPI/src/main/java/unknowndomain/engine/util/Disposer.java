package unknowndomain.engine.util;

public interface Disposer {

    Disposable register(Object obj, Runnable action);
}
