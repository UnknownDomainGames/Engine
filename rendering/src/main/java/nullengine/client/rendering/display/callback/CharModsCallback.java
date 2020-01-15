package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.input.Modifiers;

@FunctionalInterface
public interface CharModsCallback {
    void invoke(Window window, char character, Modifiers mods);
}
