package engine.graphics.display.callback;

import engine.graphics.display.Window;
import engine.input.Modifiers;

@FunctionalInterface
public interface CharModsCallback {
    void invoke(Window window, char character, Modifiers mods);
}
