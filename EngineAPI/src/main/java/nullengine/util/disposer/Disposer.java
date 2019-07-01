package nullengine.util.disposer;

import java.lang.ref.Reference;

public interface Disposer {

    Reference register(Object obj, Runnable action);
}
