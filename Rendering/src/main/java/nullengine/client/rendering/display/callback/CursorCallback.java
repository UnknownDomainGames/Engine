package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface CursorCallback {
    void invoke(Window window, double xpos, double ypos);
}
