package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowPosCallback {
    void invoke(Window window, int x, int y);
}
