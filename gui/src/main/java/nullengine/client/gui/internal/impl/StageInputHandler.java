package nullengine.client.gui.internal.impl;

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
        initCallbacks();
    }

    private void initCallbacks() {
        cursorCallback = (window, xpos, ypos) -> stage.scene().ifPresent(scene ->
                scene.processCursor(xpos, ypos));
        mouseCallback = (window, button, action, mods) -> stage.scene().ifPresent(scene ->
                scene.processMouse(button, mods, action == Action.PRESS));
        keyCallback = (window, key, scancode, action, mods) -> stage.scene().ifPresent(scene ->
                scene.processKey(key, mods, action != Action.RELEASE));
        scrollCallback = (window, xoffset, yoffset) -> stage.scene().ifPresent(scene ->
                scene.processScroll(xoffset, yoffset));
        charModsCallback = (window, character, mods) -> stage.scene().ifPresent(scene ->
                scene.processCharMods(character, mods));
    }

    public void enable() {
        window.addCursorCallback(cursorCallback);
        window.addMouseCallback(mouseCallback);
        window.addKeyCallback(keyCallback);
        window.addCursorCallback(cursorCallback);
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
