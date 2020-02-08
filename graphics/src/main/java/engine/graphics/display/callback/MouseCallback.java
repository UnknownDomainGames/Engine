package engine.graphics.display.callback;

import engine.graphics.display.Window;
import engine.input.Action;
import engine.input.Modifiers;
import engine.input.MouseButton;

@FunctionalInterface
public interface MouseCallback {
    void invoke(Window window, MouseButton button, Action action, Modifiers modifiers);
}
