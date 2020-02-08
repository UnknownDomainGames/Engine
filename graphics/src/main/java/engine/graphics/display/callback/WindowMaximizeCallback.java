package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowMaximizeCallback {
    void invoke(Window window, boolean maximized);
}
