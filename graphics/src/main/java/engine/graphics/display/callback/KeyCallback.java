package engine.graphics.display.callback;

import engine.graphics.display.Window;
import engine.input.Action;
import engine.input.KeyCode;
import engine.input.Modifiers;

@FunctionalInterface
public interface KeyCallback {
    void invoke(Window window, KeyCode key, int scancode, Action action, Modifiers modifiers);
}
