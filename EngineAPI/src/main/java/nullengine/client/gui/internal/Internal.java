package nullengine.client.gui.internal;

import java.util.function.Supplier;

public final class Internal {

    private static Supplier<Context> context = () -> {
        throw new IllegalStateException("Context is uninitialized");
    };

    public static Context getContext() {
        return context.get();
    }

    /**
     * INTERNAL METHOD. DON'T CALL IT!!!
     */
    public static void setContext(Context context) {
        Internal.context = () -> context;
    }

    public static interface Context {

        ImageHelper getImageHelper();
    }
}
