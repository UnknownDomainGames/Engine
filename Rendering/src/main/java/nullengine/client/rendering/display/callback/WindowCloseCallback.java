package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface WindowCloseCallback {
    void invoke(Window window);
}