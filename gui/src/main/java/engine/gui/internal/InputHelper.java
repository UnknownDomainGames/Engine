package engine.gui.internal;

public abstract class InputHelper {

    private static InputHelper INSTANCE;

    protected static void setInstance(InputHelper instance) {
        INSTANCE = instance;
    }

    public static long getDoubleClickTime() {
        return INSTANCE._getDoubleClickTime();
    }

    public static void setDoubleClickTime(long time) {
        INSTANCE._setDoubleClickTime(time);
    }

    public abstract long _getDoubleClickTime();

    public abstract void _setDoubleClickTime(long time);
}
