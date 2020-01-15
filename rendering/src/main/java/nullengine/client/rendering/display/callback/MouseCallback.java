package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.input.Action;
import nullengine.client.rendering.display.input.Modifiers;
import nullengine.client.rendering.display.input.MouseButton;

@FunctionalInterface
public interface MouseCallback {
    void invoke(Window window, MouseButton button, Action action, Modifiers modifiers);
}
