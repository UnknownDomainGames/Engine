package nullengine.client.gui.util;

import nullengine.client.gui.Node;

public final class Utils {

    private Utils() {
    }

    public static float middleValue(float arg0, float arg1, float arg2) {
        return Math.min(Math.max(arg0, arg1), Math.max(arg1, arg2));
    }

    public static float prefWidth(Node node) {
        return middleValue(node.prefWidth(), node.minWidth(), node.maxWidth());
    }

    public static float prefHeight(Node node) {
        return middleValue(node.prefHeight(), node.minHeight(), node.maxHeight());
    }
}
