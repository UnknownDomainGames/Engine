package nullengine.client.gui.internal.impl;

import nullengine.client.gui.Scene;
import nullengine.client.gui.Stage;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.callback.*;
import nullengine.input.Action;

public final class StageInputHandler {
    private final Stage stage;
    private final Window window;

    private CursorCallback cursorCallback;
    private MouseCallback mouseCallback;
    private KeyCallback keyCallback;
    private ScrollCallback scrollCallback;
    private CharModsCallback charModsCallback;

    public StageInputHandler(Stage stage, Window window) {
        this.stage = stage;
        this.window = window;
    }

    public void enable() {
        if (cursorCallback == null) cursorCallback = (window, xpos, ypos) -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.processCursor(xpos, ypos);
            }
        };
        window.addCursorCallback(cursorCallback);
        if (mouseCallback == null) mouseCallback = (window, button, action, mods) -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.processMouse(button, mods, action == Action.PRESS);
            }
        };
        window.addMouseCallback(mouseCallback);
        if (keyCallback == null) keyCallback = (window, key, scancode, action, mods) -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.processKey(key, mods, action != Action.RELEASE);
            }
        };
        window.addKeyCallback(keyCallback);
        if (scrollCallback == null) scrollCallback = (window, xoffset, yoffset) -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.processScroll(xoffset, yoffset);
            }
        };
        window.addCursorCallback(cursorCallback);
        if (charModsCallback == null) charModsCallback = (window, character, mods) -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                scene.processCharMods(character, mods);
            }
        };
        window.addCharModsCallback(charModsCallback);
    }

    public void disable() {
        window.removeCursorCallback(cursorCallback);
        window.removeMouseCallback(mouseCallback);
        window.removeKeyCallback(keyCallback);
        window.removeScrollCallback(scrollCallback);
        window.removeCharModsCallback(charModsCallback);
    }

}
