package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface DropCallback {
    void invoke(Window window, String[] files);
}
