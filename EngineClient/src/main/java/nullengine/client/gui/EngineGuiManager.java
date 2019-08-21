package nullengine.client.gui;

import nullengine.Platform;
import nullengine.client.gui.event.KeyEvent;
import nullengine.client.input.keybinding.Key;
import nullengine.client.rendering.RenderContext;
import nullengine.util.UndoHistory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EngineGuiManager implements GuiManager {

    //TODO: review on availability of customizing limit of history
    public static final int MAX_SCENE_HISTORY = 20;
    private final RenderContext context;

    private final Map<String, Scene> huds = new HashMap<>();
    private final Map<String, Scene> unmodifiableHuds = Collections.unmodifiableMap(huds);
    private final Map<String, Scene> displayingHuds = new HashMap<>();
    private final Map<String, Scene> unmodifiableDisplayingHuds = Collections.unmodifiableMap(displayingHuds);

    private Scene displayingScreen;
    private UndoHistory<Scene> sceneHistory;
    private Consumer<KeyEvent.KeyDownEvent> escCloseHandler;

    private boolean hudVisible = true;

    public EngineGuiManager(RenderContext context) {
        this.context = context;
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
        escCloseHandler = keyDownEvent -> {
            if (keyDownEvent.getKey() == Key.KEY_ESCAPE) {
                scene.getRoot().requireClose();
            }
        };
        displayingScreen = scene;
        if(scene == null){
            return;
        }
        scene.getRoot().addEventHandler(KeyEvent.KeyDownEvent.class, escCloseHandler);
        displayingScreen.setSize(context.getWindow().getWidth(), context.getWindow().getHeight());
        displayingScreen.update();
        if (scene.getRoot() instanceof GuiTickable) {
            context.getScheduler().runTaskEveryFrame(() -> ((GuiTickable) scene.getRoot()).update(context));
        }
        context.getWindow().addCharCallback(displayingScreen.charCallback);
        context.getWindow().addCursorCallback(displayingScreen.cursorCallback);
        context.getWindow().addKeyCallback(displayingScreen.keyCallback);
        context.getWindow().addMouseCallback(displayingScreen.mouseCallback);
        context.getWindow().addScrollCallback(displayingScreen.scrollCallback);
        context.getWindow().getCursor().showCursor();
    }

    private void pushToHistory() {
        if (displayingScreen != null) {
            if (!incognito) {
                sceneHistory.pushHistory(displayingScreen);
            }
            displayingScreen.getRoot().removeEventHandler(KeyEvent.KeyDownEvent.class, escCloseHandler);
            if (displayingScreen.getRoot() instanceof GuiTickable) {
                context.getScheduler().cancelTask(() -> ((GuiTickable) displayingScreen.getRoot()).update(context));
            }
            context.getWindow().removeCharCallback(displayingScreen.charCallback);
            context.getWindow().removeCursorCallback(displayingScreen.cursorCallback);
            context.getWindow().removeKeyCallback(displayingScreen.keyCallback);
            context.getWindow().removeMouseCallback(displayingScreen.mouseCallback);
            context.getWindow().removeScrollCallback(displayingScreen.scrollCallback);
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
        context.getWindow().getCursor().disableCursor();
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
            hud.setSize(context.getWindow().getWidth(), context.getWindow().getHeight());
            hud.update();
            huds.put(id, hud);
            displayingHuds.put(id, hud);
            if (hud.getRoot() instanceof GuiTickable) {
                context.getScheduler().runTaskEveryFrame(() -> ((GuiTickable) hud.getRoot()).update(context));
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
            context.getScheduler().cancelTask(() -> ((GuiTickable) hud.getRoot()).update(context));
        }
    }

    @Override
    public void clearHuds() {
        for (Scene hud : huds.values()) {
            if (hud.getRoot() instanceof GuiTickable) {
                context.getScheduler().cancelTask(() -> ((GuiTickable) hud.getRoot()).update(context));
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
