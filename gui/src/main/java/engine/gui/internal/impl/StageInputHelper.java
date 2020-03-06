package engine.gui.internal.impl;

import com.github.mouse0w0.observable.value.ValueChangeListener;
import engine.graphics.display.Window;
import engine.graphics.display.callback.*;
import engine.gui.Stage;
import engine.gui.internal.StageHelper;
import engine.input.Action;

public final class StageInputHelper {
    private final Stage stage;
    private final Window window;

    private CursorCallback cursorCallback;
    private MouseCallback mouseCallback;
    private KeyCallback keyCallback;
    private ScrollCallback scrollCallback;
    private CharModsCallback charModsCallback;
    private ValueChangeListener<Boolean> focusListener;

    public StageInputHelper(Stage stage) {
        this.stage = stage;
        this.window = StageHelper.getWindow(stage);
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
        focusListener = (observable, oldValue, newValue) -> stage.getScene().processFocus(newValue);
    }

    public void enable() {
        window.addCursorCallback(cursorCallback);
        window.addMouseCallback(mouseCallback);
        window.addKeyCallback(keyCallback);
        window.addCursorCallback(cursorCallback);
        window.addCharModsCallback(charModsCallback);
        stage.focused().addChangeListener(focusListener);
    }

    public void disable() {
        window.removeCursorCallback(cursorCallback);
        window.removeMouseCallback(mouseCallback);
        window.removeKeyCallback(keyCallback);
        window.removeScrollCallback(scrollCallback);
        window.removeCharModsCallback(charModsCallback);
        stage.focused().removeChangeListener(focusListener);
    }

}
