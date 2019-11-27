package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface KeyCallback {
    void invoke(Window window, int key, int scancode, int action, int mods);
}
