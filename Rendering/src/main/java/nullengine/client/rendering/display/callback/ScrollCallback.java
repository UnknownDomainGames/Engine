package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface ScrollCallback {
    void invoke(Window window, double xoffset, double yoffset);
}
