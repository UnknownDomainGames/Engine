package engine.gui;

import com.github.mouse0w0.observable.value.*;
import engine.gui.event.*;
import engine.gui.input.*;
import engine.gui.internal.SceneHelper;
import engine.gui.stage.Stage;
import engine.input.KeyCode;
import engine.input.Modifiers;
import engine.input.MouseButton;
import engine.util.Color;
import org.apache.commons.lang3.Validate;

import java.nio.file.Path;
import java.util.*;

import static engine.gui.internal.InputHelper.getDoubleClickTime;

public class Scene implements EventTarget {

    private final MutableFloatValue width = new SimpleMutableFloatValue();
    private final MutableFloatValue height = new SimpleMutableFloatValue();

    final MutableObjectValue<Stage> stage = new SimpleMutableObjectValue<>();

    private final MutableObjectValue<Color> fill = new SimpleMutableObjectValue<>(Color.BLACK);

    private final MutableObjectValue<Parent> root = new SimpleMutableObjectValue<>();

    private final EventHandlerManager eventHandlerManager = new EventHandlerManager();

    static {
        SceneHelper.setSceneAccessor(new SceneHelper.SceneAccessor() {
            @Override
            public void setStage(Scene scene, Stage stage) {
                scene.stage.set(stage);
            }

            @Override
            public void setSize(Scene scene, float width, float height) {
                scene.setSize(width, height);
            }

            @Override
            public void preferredSize(Scene scene) {
                scene.preferredSize();
            }
        });
    }

    public Scene(Parent root) {
        setRoot(root);
    }

    public final ObservableFloatValue width() {
        return width.toUnmodifiable();
    }

    public final float getWidth() {
        return width.get();
    }

    public final ObservableFloatValue height() {
        return height.toUnmodifiable();
    }

    public final float getHeight() {
        return height.get();
    }

    private void setSize(float width, float height) {
        this.width.set(width);
        this.height.set(height);
        updateRoot();
    }

    private void preferredSize() {
        Parent root = getRoot();
        float width = root.getLayoutX() + Parent.prefWidth(root);
        float height = root.getLayoutY() + Parent.prefHeight(root);
        setSize(width, height);
    }

    public ObservableObjectValue<Stage> stage() {
        return stage.toUnmodifiable();
    }

    public Stage getStage() {
        return stage.get();
    }

    public ObservableObjectValue<Parent> root() {
        return root.toUnmodifiable();
    }

    public Parent getRoot() {
        return root.get();
    }

    public void setRoot(Parent root) {
        Validate.notNull(root);
        if (root.getParent() != null) {
            throw new IllegalStateException(root + " is already inside a container and cannot be set as root");
        }

        this.root.set(root);
        root.scene.set(this);
        updateRoot();
    }

    private void updateRoot() {
        Parent root = getRoot();
        root.resize(getWidth() - root.getLayoutX(), getHeight() - root.getLayoutY());
        root.needsLayout();
    }

    public void update() {
        getRoot().layout();
    }

    public MutableObjectValue<Color> fill() {
        return fill;
    }

    public Color getFill() {
        return fill.orElse(Color.BLACK);
    }

    public void setFill(Color color) {
        fill.set(color);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
    }

    private List<Node> raycast(float x, float y, boolean keepParent) {
        List<Node> results = new ArrayList<>();
        raycast(getRoot(), x, y, results, keepParent);
        return results;
    }

    private void raycast(Parent parent, float x, float y, List<Node> results, boolean keepParent) {
        boolean noMatchingChild = true;
        for (ListIterator<Node> iterator = parent.getUnmodifiableChildren().listIterator(parent.getUnmodifiableChildren().size()); iterator.hasPrevious(); ) {
            Node node = iterator.previous();
            if (!node.isVisible() || node.isDisabled() || !node.contains(x, y)) continue;

            noMatchingChild = false;
            if (node instanceof Parent) {
                raycast((Parent) node, x - node.getLayoutX(), y - node.getLayoutY(), results, keepParent);
            } else {
                results.add(node);
            }
        }

        if (keepParent || noMatchingChild) {
            results.add(parent);
        }
    }

    private float cursorX = Float.NaN;
    private float cursorY = Float.NaN;

    private final Set<Node> hoveredNodes = new HashSet<>();

    public void processCursor(double xPos, double yPos) {
        Stage stage = getStage();
        cursorX = (float) xPos / stage.getScaleX();
        cursorY = (float) yPos / stage.getScaleY();

        var hitNodes = raycast(cursorX, cursorY, true);
        var lostHoveredNodes = new HashSet<>(hoveredNodes);
        lostHoveredNodes.removeAll(hitNodes);
        for (var node : lostHoveredNodes) {
            var pos = node.relativePos(cursorX, cursorY);
            new MouseEvent(MouseEvent.MOUSE_EXITED, node, pos.getX(), pos.getY(), cursorX, cursorY).fireEvent();
        }
        hoveredNodes.removeAll(lostHoveredNodes);

        for (var node : hitNodes) {
            var pos = node.relativePos(cursorX, cursorY);
            var eventType = hoveredNodes.add(node) ? MouseEvent.MOUSE_ENTERED : MouseEvent.MOUSE_MOVED;
            new MouseEvent(eventType, node, pos.getX(), pos.getY(), cursorX, cursorY).fireEvent();
        }
    }

    private final Set<Node> focusedNodes = new HashSet<>();
    private final Map<MouseButton, PressedButton> pressedNodes = new HashMap<>();

    private static final class PressedButton {
        private long lastPressedTime;
        private int clickCount;
        private List<Node> pressedNodes = new ArrayList<>();

        public void onPressed() {
            long currentPressedTime = System.currentTimeMillis();
            if (currentPressedTime - lastPressedTime <= getDoubleClickTime()) clickCount++;
            else clickCount = 1; // timeout, reset click count.
            lastPressedTime = currentPressedTime;
        }

        public int getClickCount() {
            return clickCount;
        }

        public List<Node> getPressedNodes() {
            return pressedNodes;
        }

        public void addPressedNode(Node node) {
            pressedNodes.add(node);
        }

        public void clearPressedNodes() {
            pressedNodes.clear();
        }
    }

    public void processMouse(MouseButton button, Modifiers modifier, boolean pressed) {
        if (Float.isNaN(cursorX) || Float.isNaN(cursorY)) return;

        var hitNodes = raycast(cursorX, cursorY, false);

        if (pressed) {
            for (var node : hitNodes) {
                if (node.isDisabled()) continue;
                if (focusedNodes.contains(node)) continue;
                focusedNodes.add(node);
                node.focused.set(true);
            }
            List<Node> lostFocusNodes = new ArrayList<>(focusedNodes);
            lostFocusNodes.removeAll(hitNodes);
            focusedNodes.removeAll(lostFocusNodes);
            lostFocusNodes.forEach(node -> node.focused.set(false));
        }

        PressedButton pressedButton = pressedNodes.computeIfAbsent(button, key -> new PressedButton());
        if (pressed) {
            pressedButton.onPressed();
            for (var node : hitNodes) {
                pressedButton.addPressedNode(node);
                var pos = node.relativePos(cursorX, cursorY);
                new MouseActionEvent(MouseActionEvent.MOUSE_PRESSED, node, pos.getX(), pos.getY(), cursorX, cursorY, button, modifier, 0).fireEvent();
            }
        } else {
            for (Node node : pressedButton.getPressedNodes()) {
                var pos = node.relativePos(cursorX, cursorY);
                new MouseActionEvent(MouseActionEvent.MOUSE_RELEASED, node, pos.getX(), pos.getY(), cursorX, cursorY, button, modifier, 0).fireEvent();
                new MouseActionEvent(MouseActionEvent.MOUSE_CLICKED, node, pos.getX(), pos.getY(), cursorX, cursorY, button, modifier, pressedButton.getClickCount()).fireEvent();
            }
            pressedButton.clearPressedNodes();
        }
    }

    public void processScroll(double xOffset, double yOffset) {
        if (focusedNodes.isEmpty()) {
            new ScrollEvent(ScrollEvent.ANY, this, cursorX, cursorY, cursorX, cursorY, xOffset, yOffset).fireEvent();
        } else {
            focusedNodes.forEach(node -> {
                var pos = node.relativePos(cursorX, cursorY);
                new ScrollEvent(ScrollEvent.ANY, node, pos.getX(), pos.getY(), cursorX, cursorY, xOffset, yOffset).fireEvent();
            });
        }
    }

    public void processKey(KeyCode key, Modifiers modifier, boolean pressed) {
        if (focusedNodes.isEmpty()) {
            new KeyEvent(pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, this, key, modifier, pressed).fireEvent();
        } else {
            focusedNodes.forEach(node ->
                    new KeyEvent(pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, node, key, modifier, pressed).fireEvent());
        }
    }

    public void processCharMods(char codePoint, Modifiers modifier) {
        if (focusedNodes.isEmpty()) {
            new KeyEvent(KeyEvent.KEY_TYPED, this, KeyCode.NONE, String.valueOf(codePoint), modifier, true).fireEvent();
        } else {
            focusedNodes.forEach(node ->
                    new KeyEvent(KeyEvent.KEY_TYPED, node, KeyCode.NONE, String.valueOf(codePoint), modifier, true).fireEvent());
        }
    }

    public void processDrop(List<Path> paths) {
        if (hoveredNodes.isEmpty()) {
            new DropEvent(DropEvent.DROP, this, cursorX, cursorY, cursorX, cursorY, paths).fireEvent();
        } else {
            for (Node node : hoveredNodes) {
                var pos = node.relativePos(cursorX, cursorY);
                new DropEvent(DropEvent.DROP, node, pos.getX(), pos.getY(), cursorX, cursorY, paths).fireEvent();
            }
        }
    }

    public void processFocus(boolean focused) {
        if (!focused) {
            // lost focus of all focused nodes.
            focusedNodes.forEach(node -> node.focused.set(false));
            focusedNodes.clear();

            // lost hover of all hovered nodes.
            hoveredNodes.forEach(node -> {
                var pos = node.relativePos(cursorX, cursorY);
                new MouseEvent(MouseEvent.MOUSE_EXITED, node, pos.getX(), pos.getY(), cursorX, cursorY).fireEvent();
            });
            hoveredNodes.clear();
        }
    }

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    // ===== Event handlers =====
    public MutableObjectValue<EventHandler<MouseEvent>> onMouseEntered() {
        return eventHandlerManager.onMouseEntered();
    }

    public EventHandler<MouseEvent> getOnMouseEntered() {
        return eventHandlerManager.getOnMouseEntered();
    }

    public void setOnMouseEntered(EventHandler<MouseEvent> onMouseEntered) {
        eventHandlerManager.setOnMouseEntered(onMouseEntered);
    }

    public MutableObjectValue<EventHandler<MouseEvent>> onMouseExited() {
        return eventHandlerManager.onMouseExited();
    }

    public EventHandler<MouseEvent> getOnMouseExited() {
        return eventHandlerManager.getOnMouseExited();
    }

    public void setOnMouseExited(EventHandler<MouseEvent> onMouseExited) {
        eventHandlerManager.setOnMouseExited(onMouseExited);
    }

    public MutableObjectValue<EventHandler<MouseEvent>> onMouseMoved() {
        return eventHandlerManager.onMouseMoved();
    }

    public EventHandler<MouseEvent> getOnMouseMoved() {
        return eventHandlerManager.getOnMouseMoved();
    }

    public void setOnMouseMoved(EventHandler<MouseEvent> onMouseMoved) {
        eventHandlerManager.setOnMouseMoved(onMouseMoved);
    }

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMousePressed() {
        return eventHandlerManager.onMousePressed();
    }

    public EventHandler<MouseActionEvent> getOnMousePressed() {
        return eventHandlerManager.getOnMousePressed();
    }

    public void setOnMousePressed(EventHandler<MouseActionEvent> onMousePressed) {
        eventHandlerManager.setOnMousePressed(onMousePressed);
    }

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMouseReleased() {
        return eventHandlerManager.onMouseReleased();
    }

    public EventHandler<MouseActionEvent> getOnMouseReleased() {
        return eventHandlerManager.getOnMouseReleased();
    }

    public void setOnMouseReleased(EventHandler<MouseActionEvent> onMouseReleased) {
        eventHandlerManager.setOnMouseReleased(onMouseReleased);
    }

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMouseClicked() {
        return eventHandlerManager.onMouseClicked();
    }

    public EventHandler<MouseActionEvent> getOnMouseClicked() {
        return eventHandlerManager.getOnMouseClicked();
    }

    public void setOnMouseClicked(EventHandler<MouseActionEvent> onMouseClicked) {
        eventHandlerManager.setOnMouseClicked(onMouseClicked);
    }

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyPressed() {
        return eventHandlerManager.onKeyPressed();
    }

    public EventHandler<KeyEvent> getOnKeyPressed() {
        return eventHandlerManager.getOnKeyPressed();
    }

    public void setOnKeyPressed(EventHandler<KeyEvent> onKeyPressed) {
        eventHandlerManager.setOnKeyPressed(onKeyPressed);
    }

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyReleased() {
        return eventHandlerManager.onKeyReleased();
    }

    public EventHandler<KeyEvent> getOnKeyReleased() {
        return eventHandlerManager.getOnKeyReleased();
    }

    public void setOnKeyReleased(EventHandler<KeyEvent> onKeyReleased) {
        eventHandlerManager.setOnKeyReleased(onKeyReleased);
    }

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyTyped() {
        return eventHandlerManager.onKeyTyped();
    }

    public EventHandler<KeyEvent> getOnKeyTyped() {
        return eventHandlerManager.getOnKeyTyped();
    }

    public void setOnKeyTyped(EventHandler<KeyEvent> onKeyTyped) {
        eventHandlerManager.setOnKeyTyped(onKeyTyped);
    }

    public MutableObjectValue<EventHandler<ScrollEvent>> onScroll() {
        return eventHandlerManager.onScroll();
    }

    public EventHandler<ScrollEvent> getOnScroll() {
        return eventHandlerManager.getOnScroll();
    }

    public void setOnScroll(EventHandler<ScrollEvent> onScroll) {
        eventHandlerManager.setOnScroll(onScroll);
    }
}
