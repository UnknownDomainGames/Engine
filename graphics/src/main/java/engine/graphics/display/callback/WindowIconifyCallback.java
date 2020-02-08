package engine.graphics.display.callback;

import engine.graphics.display.Window;

@FunctionalInterface
public interface WindowIconifyCallback {
    void invoke(Window window, boolean iconified);
}
