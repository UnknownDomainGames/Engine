package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface MouseCallback {
    void invoke(Window window, int button, int action, int mods);
}
