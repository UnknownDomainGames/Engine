package nullengine.client.gui.internal;

import nullengine.exception.NoInitializationException;

public final class Internal {

    private static Context context;

    public static Context getContext() {
        if (context == null) {
            throw new NoInitializationException();
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
