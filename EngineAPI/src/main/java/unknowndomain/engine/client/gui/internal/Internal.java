package unknowndomain.engine.client.gui.internal;

import unknowndomain.engine.exception.NotInitializationException;

public final class Internal {

    private static Context context;

    public static Context getContext() {
        if (context == null) {
            throw new NotInitializationException();
        }
        return context;
    }

    /**
     * INTERNAL METHOD. DON'T CALL IT!!!
     */
    public static void setContext(Context context) {
        Internal.context = context;
    }

    public static interface Context {

        FontHelper getFontHelper();

        ImageHelper getImageHelper();
    }
}
