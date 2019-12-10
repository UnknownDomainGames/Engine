package nullengine.client.gui;

import nullengine.Platform;
import nullengine.client.gui.event.old.KeyEvent_;
import nullengine.client.input.keybinding.Key;
import nullengine.client.rendering.display.Window;
import nullengine.util.UndoHistory;

import java.util.*;
import java.util.function.Consumer;

public class EngineGuiManager implements GuiManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;
    private final Window window;

    private final Map<String, Scene> huds = new HashMap<>();
    private final Map<String, Scene> unmodifiableHuds = Collections.unmodifiableMap(huds);
    private final Map<String, Scene> displayingHuds = new HashMap<>();
    private final Map<String, Scene> unmodifiableDisplayingHuds = Collections.unmodifiableMap(displayingHuds);

    private final List<GuiTickable> tickables = new ArrayList<>();

    private Scene displayingScreen;
    private UndoHistory<Scene> sceneHistory;
    private Consumer<KeyEvent_.KeyDownEvent> escCloseHandler;

    private boolean hudVisible = true;

    public EngineGuiManager(Window window) {
        this.window = window;
        sceneHistory = new UndoHistory<>(MAX_SCENE_HISTORY);
    }

    private boolean incognito = false;

    public void doTick() {
        tickables.forEach(GuiTickable::update);
    }

    @Override
    public void showScreen(Scene scene) {
        showScreenInternal(scene);
        incognito = false;
    }

    private void showScreenInternal(Scene scene) {
        pushToHistory();
        // TODO: Remove it
        escCloseHandler = keyDownEvent -> {
            if (keyDownEvent.getKey() == Key.KEY_ESCAPE) {
                scene.getRoot().requireClose();
            }
        };
        displayingScreen = scene;
        if (scene == null) {
            return;
        }
        scene.getRoot().addEventHandler(KeyEvent_.KeyDownEvent.class, escCloseHandler);
        var widthScaleless = window.getWidth() / window.getContentScaleX();
        var heightScaleless = window.getHeight() / window.getContentScaleY();
        displayingScreen.setSize(widthScaleless, heightScaleless);
        displayingScreen.update();
        if (scene.getRoot() instanceof GuiTickable) {
            tickables.add((GuiTickable) scene.getRoot());
        }
        window.addCharCallback(displayingScreen.charCallback);
        window.addCursorCallback(displayingScreen.cursorCallback);
        window.addKeyCallback(displayingScreen.keyCallback);
        window.addMouseCallback(displayingScreen.mouseCallback);
        window.addScrollCallback(displayingScreen.scrollCallback);
        window.getCursor().showCursor();
    }

    private void pushToHistory() {
        if (displayingScreen != null) {
            if (!incognito) {
                sceneHistory.pushHistory(displayingScreen);
            }
            displayingScreen.getRoot().removeEventHandler(KeyEvent_.KeyDownEvent.class, escCloseHandler);
            if (displayingScreen.getRoot() instanceof GuiTickable) {
                tickables.remove(displayingScreen.getRoot());
            }
            window.removeCharCallback(displayingScreen.charCallback);
            window.removeCursorCallback(displayingScreen.cursorCallback);
            window.removeKeyCallback(displayingScreen.keyCallback);
            window.removeMouseCallback(displayingScreen.mouseCallback);
            window.removeScrollCallback(displayingScreen.scrollCallback);
        }
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
            hud.setSize(window.getWidth(), window.getHeight());
            hud.update();
            huds.put(id, hud);
            displayingHuds.put(id, hud);
            if (hud.getRoot() instanceof GuiTickable) {
                tickables.add((GuiTickable) hud.getRoot());
            }
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
        if (hud.getRoot() instanceof GuiTickable) {
            tickables.remove(hud.getRoot());
        }
    }

    @Override
    public void clearHuds() {
        for (Scene hud : huds.values()) {
            if (hud.getRoot() instanceof GuiTickable) {
                tickables.remove(hud.getRoot());
            }
        }
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
