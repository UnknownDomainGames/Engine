package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface DropCallback {
    void invoke(Window window, String[] files);
}
