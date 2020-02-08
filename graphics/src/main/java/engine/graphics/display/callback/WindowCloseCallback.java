package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowCloseCallback {
    void invoke(Window window);
}
