package unknowndomain.engine.client.gui;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.util.UndoHistory;

import java.util.HashMap;
import java.util.Map;

public class EngineGuiManager implements GuiManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;
    private final EngineClient engine;

    private Map<String, Scene> huds;
    private Scene displayingScreen;
    private UndoHistory<Scene> sceneHistory;

    public EngineGuiManager(EngineClient engine) {
        this.engine = engine;
        huds = new HashMap<>();
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
        displayingScreen = scene;
        engine.getWindow().addCharCallback(displayingScreen.charCallback);
        engine.getWindow().addCursorCallback(displayingScreen.cursorCallback);
        engine.getWindow().addKeyCallback(displayingScreen.keyCallback);
        engine.getWindow().addMouseCallback(displayingScreen.mouseCallback);
        engine.getWindow().addScrollCallback(displayingScreen.scrollCallback);
        engine.getWindow().getCursor().showCursor();
    }

    private void pushToHistory() {
        if (displayingScreen != null) {
            if (!incognito) {
                sceneHistory.pushHistory(displayingScreen);
            }
            engine.getWindow().removeCharCallback(displayingScreen.charCallback);
            engine.getWindow().removeCursorCallback(displayingScreen.cursorCallback);
            engine.getWindow().removeKeyCallback(displayingScreen.keyCallback);
            engine.getWindow().removeMouseCallback(displayingScreen.mouseCallback);
            engine.getWindow().removeScrollCallback(displayingScreen.scrollCallback);
        }
    }

    public void showIncognitoScreen(Scene scene) {
        showScreenInternal(scene);
        incognito = true;
    }

    @Override
    public void showLastScreen() {
        var lastscreen = sceneHistory.undo();
        showIncognitoScreen(lastscreen);
    }

    @Override
    public void showHud(String id, Scene hud) {
        if (huds.containsKey(id)) {
            Platform.getLogger().warn(String.format("Conflicting HUD id!: %s", id));
        } else {
            huds.put(id, hud);
        }
    }

    @Override
    public void closeScreen() {
        pushToHistory();
        displayingScreen = null;
        engine.getWindow().getCursor().disableCursor();
    }

    @Override
    public void hideHud(String id) {
        huds.remove(id);
    }

    @Override
    public Map<String, Scene> getHuds() {
        return huds;
    }

    @Override
    public Scene getDisplayingScreen() {
        return displayingScreen;
    }
}
