package nullengine.client.gui.util;

import nullengine.client.gui.Component;

public final class Utils {

    private Utils() {
    }

    public static float middleValue(float arg0, float arg1, float arg2) {
        return Math.min(Math.max(arg0, arg1), Math.max(arg1, arg2));
    }

    public static float prefWidth(Component component) {
        return middleValue(component.prefWidth(), component.minWidth(), component.maxWidth());
    }

    public static float prefHeight(Component component) {
        return middleValue(component.prefHeight(), component.minHeight(), component.maxHeight());
    }
}
