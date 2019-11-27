package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface WindowPosCallback {
    void invoke(Window window, int x, int y);
}
