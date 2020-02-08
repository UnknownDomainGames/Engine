package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowSizeCallback {
    void invoke(Window window, int width, int height);
}
