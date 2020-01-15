package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.input.Action;
import nullengine.client.rendering.display.input.KeyCode;
import nullengine.client.rendering.display.input.Modifiers;

@FunctionalInterface
public interface KeyCallback {
    void invoke(Window window, KeyCode key, int scancode, Action action, Modifiers modifiers);
}
