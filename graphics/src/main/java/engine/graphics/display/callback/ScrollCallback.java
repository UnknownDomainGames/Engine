package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface ScrollCallback {
    void invoke(Window window, double xoffset, double yoffset);
}
