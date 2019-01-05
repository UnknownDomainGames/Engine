package unknowndomain.engine.client.gui.internal;

public final class Internal {

    private static Context context;

    public static Context getContext() {
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
    }
}
