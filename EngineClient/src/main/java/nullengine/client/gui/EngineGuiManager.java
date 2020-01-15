package nullengine.client.gui;

import nullengine.Platform;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.callback.*;
import nullengine.input.Action;
import nullengine.util.UndoHistory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EngineGuiManager implements GuiManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;
    private final Window window;

    private final Map<String, Scene> huds = new HashMap<>();
    private final Map<String, Scene> unmodifiableHuds = Collections.unmodifiableMap(huds);
    private final Map<String, Scene> displayingHuds = new HashMap<>();
    private final Map<String, Scene> unmodifiableDisplayingHuds = Collections.unmodifiableMap(displayingHuds);

    private Scene displayingScreen;
    private UndoHistory<Scene> sceneHistory;
//    private EventHandler<KeyEvent> escCloseHandler;

    private boolean hudVisible = true;

    private final CursorCallback cursorCallback = (window1, xpos, ypos) -> {
        if (displayingScreen != null) {
            displayingScreen.processCursor(xpos, ypos);
        }
    };
    private final MouseCallback mouseCallback = (window1, button, action, mods) -> {
        if (displayingScreen != null && action != Action.REPEAT) {
            displayingScreen.processMouse(button, mods, action == Action.PRESS);
        }
    };
    private final KeyCallback keyCallback = (window1, key, scancode, action, mods) -> {
        if (displayingScreen != null) {
            displayingScreen.processKey(key, mods, action != Action.RELEASE);
        }
    };
    private final ScrollCallback scrollCallback = (window1, xoffset, yoffset) -> {
        if (displayingScreen != null) {
            displayingScreen.processScroll(xoffset, yoffset);
        }
    };
    private final CharModsCallback charModsCallback = (window1, character, mods) -> {
        if (displayingScreen != null) {
            displayingScreen.processCharMods(character, mods);
        }
    };
    private final WindowSizeCallback windowSizeCallback = (window1, width, height) -> {
        float uiScale = Platform.getEngineClient().getSettings().getDisplaySettings().getUiScalePercentage();
        if (displayingScreen != null) {
            displayingScreen.setViewport(width, height, uiScale, uiScale);
            displayingScreen.update();
        }

        float hudScale = Platform.getEngineClient().getSettings().getDisplaySettings().getHudScalePercentage();
        huds.values().forEach(scene -> {
            scene.setViewport(width, height, hudScale, hudScale);
            scene.update();
        });
    };

    public EngineGuiManager(Window window) {
        this.window = window;
        window.addCursorCallback(cursorCallback);
        window.addMouseCallback(mouseCallback);
        window.addKeyCallback(keyCallback);
        window.addScrollCallback(scrollCallback);
        window.addCharModsCallback(charModsCallback);
        window.addWindowSizeCallback(windowSizeCallback);
        sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);
    }

    private boolean incognito = false;

    @Override
    public void showScreen(Scene scene) {
        showScreenInternal(scene);
        incognito = false;
    }

    private void showScreenInternal(Scene scene) {
        pushToHistory();
        // TODO: Remove it
//        escCloseHandler = event -> {
//            if (event.getKey() == KeyCode.KEY_ESCAPE) {
//                scene.getRoot().requireClose();
//            }
//        };
        displayingScreen = scene;
        if (scene == null) return;
//        scene.addEventHandler(KeyEvent.KEY_PRESSED, escCloseHandler);
        var uiScale = Platform.getEngineClient().getSettings().getDisplaySettings().getUiScalePercentage();
        displayingScreen.setViewport(window.getWidth(), window.getHeight(), uiScale, uiScale);
        displayingScreen.update();
        window.getCursor().showCursor();
    }

    private void pushToHistory() {
        if (displayingScreen == null) return;
        if (!incognito) {
            sceneHistory.pushHistory(displayingScreen);
        }
//        displayingScreen.removeEventHandler(KeyEvent.KEY_PRESSED, escCloseHandler);
    }

    public void showIncognitoScreen(Scene scene) {
        showScreenInternal(scene);
        incognito = true;
    }

    @Override
    public void showLastScreen() {
        var lastscreen = sceneHistory.undo();
        incognito = true;
        showScreen(lastscreen);
    }

    @Override
    public void closeScreen() {
        pushToHistory();
        displayingScreen = null;
        window.getCursor().disableCursor();
    }

    @Override
    public Scene getDisplayingScreen() {
        return displayingScreen;
    }

    @Override
    public boolean isDisplayingScreen() {
        return displayingScreen != null;
    }

    @Override
    public void toggleHudVisible() {
        setHudVisible(!isHudVisible());
    }

    @Override
    public void setHudVisible(boolean visible) {
        hudVisible = visible;
    }

    @Override
    public boolean isHudVisible() {
        return hudVisible;
    }

    @Override
    public void showHud(String id, Scene hud) {
        Scene currentHud = huds.get(id);
        if (currentHud != null) {
            Platform.getLogger().debug("Conflicted HUD id {}", id);
            currentHud.getRoot().visible().set(true);
        } else {
            var scale = Platform.getEngineClient().getSettings().getDisplaySettings().getHudScalePercentage();
            hud.setViewport(window.getWidth(), window.getHeight(), scale, scale);
            hud.update();
            huds.put(id, hud);
            displayingHuds.put(id, hud);
        }
    }

    @Override
    public void showHud(String id) {
        Scene hud = huds.get(id);
        if (hud != null) {
            displayingHuds.put(id, hud);
        }
    }

    @Override
    public void hideHud(String id) {
        displayingHuds.remove(id);
    }

    @Override
    public void removeHud(String id) {
        hideHud(id);
        Scene hud = huds.remove(id);
    }

    @Override
    public void clearHuds() {
        huds.clear();
    }

    @Override
    public Map<String, Scene> getHuds() {
        return unmodifiableHuds;
    }

    @Override
    public Map<String, Scene> getDisplayingHuds() {
        return unmodifiableDisplayingHuds;
    }
}
