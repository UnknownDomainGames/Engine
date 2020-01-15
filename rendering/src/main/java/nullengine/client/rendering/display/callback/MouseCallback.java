package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;
import nullengine.input.Action;
import nullengine.input.Modifiers;
import nullengine.input.MouseButton;

@FunctionalInterface
public interface MouseCallback {
    void invoke(Window window, MouseButton button, Action action, Modifiers modifiers);
}
