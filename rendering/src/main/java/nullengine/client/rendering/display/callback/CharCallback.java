package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface CharCallback {
    void invoke(Window window, int codepoint);
}
