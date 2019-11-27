package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface WindowFocusCallback {
    void invoke(Window window, boolean focused);
}
