package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;
import nullengine.input.Action;
import nullengine.input.KeyCode;
import nullengine.input.Modifiers;

@FunctionalInterface
public interface KeyCallback {
    void invoke(Window window, KeyCode key, int scancode, Action action, Modifiers modifiers);
}
