package nullengine.client.gui;

import nullengine.client.rendering.display.Window;
import nullengine.util.UndoHistory;

public class EngineGUIManager implements GUIManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;

    private Window window;
    private Stage stage;

    private Scene showingScene;
    private UndoHistory<Scene> sceneHistory;

    private boolean incognito = false;

    public EngineGUIManager(Window window, Stage stage) {
        this.window = window;
        this.stage = stage;
        sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);
    }

    @Override
    public void show(Scene scene) {
        showInternal(scene);
        incognito = false;
    }

    private void showInternal(Scene scene) {
        pushToHistory();
        showingScene = scene;
        if (scene == null) {
            window.getCursor().disableCursor();
            return;
        }
        stage.setScene(scene);
        window.getCursor().showCursor();
    }

    private void pushToHistory() {
        if (showingScene == null) {
            return;
        }
        if (!incognito) {
            sceneHistory.pushHistory(showingScene);
        }
    }

    public void showIncognitoScene(Scene scene) {
        showInternal(scene);
        incognito = true;
    }

    @Override
    public void showLast() {
        var lastScene = sceneHistory.undo();
        showIncognitoScene(lastScene);
    }

    @Override
    public void close() {
        showInternal(null);
    }

    @Override
    public Scene getShowingScene() {
        return showingScene;
    }

    @Override
    public boolean isShowing() {
        return showingScene != null;
    }
}
