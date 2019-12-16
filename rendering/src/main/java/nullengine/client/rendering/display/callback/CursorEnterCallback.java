package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface CursorEnterCallback {
    void invoke(Window window, boolean entered);
}
