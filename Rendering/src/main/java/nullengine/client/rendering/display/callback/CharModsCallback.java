package nullengine.client.rendering.display.callback;

import nullengine.client.rendering.display.Window;

@FunctionalInterface
public interface CharModsCallback {
    void invoke(Window window, int codepoint, int mods);
}
