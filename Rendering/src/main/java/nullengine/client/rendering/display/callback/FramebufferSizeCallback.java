package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface FramebufferSizeCallback {
    void invoke(Window window, int width, int height);
}
