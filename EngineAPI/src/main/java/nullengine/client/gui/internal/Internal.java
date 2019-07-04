package nullengine.client.gui.internal;

import nullengine.exception.UninitializationException;

import java.util.function.Supplier;

public final class Internal {

    private static Supplier<Context> context = UninitializationException.supplier("Context is uninitialized");

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

        FontHelper getFontHelper();

        ImageHelper getImageHelper();
    }
}
