package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface CursorEnterCallback {
    void invoke(Window window, boolean entered);
}
