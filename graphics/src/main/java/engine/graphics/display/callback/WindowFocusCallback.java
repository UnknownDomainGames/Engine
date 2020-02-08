package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowFocusCallback {
    void invoke(Window window, boolean focused);
}
