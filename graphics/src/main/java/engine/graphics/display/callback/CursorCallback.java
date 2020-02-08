package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface CursorCallback {
    void invoke(Window window, double xpos, double ypos);
}
