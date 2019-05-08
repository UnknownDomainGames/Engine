package unknowndomain.engine.util.disposer;

public interface Disposer {

    Disposable register(Object obj, Runnable action);
}
