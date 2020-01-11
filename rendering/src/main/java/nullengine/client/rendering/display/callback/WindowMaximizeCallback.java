package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface WindowMaximizeCallback {
    void invoke(Window window, boolean maximized);
}
