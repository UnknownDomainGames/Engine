package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.*;
import nullengine.client.gui.input.*;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scene implements EventTarget {

    private final MutableFloatValue width = new SimpleMutableFloatValue();
    private final MutableFloatValue height = new SimpleMutableFloatValue();

    private final MutableFloatValue scaleX = new SimpleMutableFloatValue(1);
    private final MutableFloatValue scaleY = new SimpleMutableFloatValue(1);

    private final MutableObjectValue<Parent> root = new SimpleMutableObjectValue<>();

    private final EventHandlerManager eventHandlerManager = new EventHandlerManager();

    public Scene(Parent root) {
        setRoot(root);
    }

    public ObservableFloatValue width() {
        return width.toUnmodifiable();
    }

    public float getWidth() {
        return width.get();
    }

    public ObservableFloatValue height() {
        return height.toUnmodifiable();
    }

    public float getHeight() {
        return height.get();
    }

    public void setSize(float width, float height) {
        this.width.set(width);
        this.height.set(height);
        updateRoot();
    }

    public float getContentScaleX() {
        return scaleX.get();
    }

    public float getContentScaleY() {
        return scaleY.get();
    }

    public void setContentScale(float x, float y) {
        if (x == 0 || y == 0) return;
        this.scaleX.set(x);
        this.scaleY.set(y);
    }

    public ObservableObjectValue<Parent> root() {
        return root.toUnmodifiable();
    }

    public Parent getRoot() {
        return root.get();
    }

    public void setRoot(Parent root) {
        Validate.notNull(root);
        if (root.parent().get() != null) {
            throw new IllegalStateException(root + " is already inside a container and cannot be set as root");
        }

        this.root.set(root);
        root.scene.setValue(this);
        updateRoot();
    }

    private void updateRoot() {
        Parent root = this.root.get();
        root.width.set(getWidth() - root.x().get());
        root.height.set(getHeight() - root.y().get());
        root.needsLayout();
    }

    public void update() {
        root.get().layout();
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
    }

    private float lastScreenX = Float.NaN;
    private float lastScreenY = Float.NaN;

    public void processCursor(double xPos, double yPos) {
        var screenX = (float) xPos / scaleX.get();
        var screenY = (float) yPos / scaleY.get();
        if (!Float.isNaN(lastScreenX) && !Float.isNaN(lastScreenY)) {
            var root = this.root.get();
            var lastNodes = root.getPointingComponents(lastScreenX, lastScreenY);
            var currentNodes = root.getPointingComponents(screenX, screenY);
            for (var node : currentNodes) {
                if (lastNodes.contains(node)) {
                    var pair = node.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_MOVED, node, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                } else {
                    var pair = node.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_ENTERED, node, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                }
            }
            lastNodes.removeAll(currentNodes);
            for (var node : lastNodes) {
                var pair = node.relativePos(screenX, screenY);
                new MouseEvent(MouseEvent.MOUSE_EXITED, node, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
            }
        }
        lastScreenX = screenX;
        lastScreenY = screenY;
    }

    private final Set<Node> focused = new HashSet<>();

    public void processMouse(MouseButton button, Modifiers modifier, boolean pressed) {
        if (!Float.isNaN(lastScreenX) && !Float.isNaN(lastScreenY)) {
            var root = this.root.get();
            var nodes = root.getPointingComponents(lastScreenX, lastScreenY);
            if (pressed) {
                for (var node : nodes) {
                    if (node.disabled.get()) continue;
                    if (focused.contains(node)) continue;
                    focused.add(node);
                    node.focused.set(true);
                }
                List<Node> focusedList = new ArrayList<>(focused);
                focusedList.removeAll(nodes);
                focused.removeAll(focusedList);
                focusedList.forEach(node -> node.focused.set(false));
            }
            for (var node : nodes) {
                if (pressed) {
                    var pair = node.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_PRESSED, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                } else {
                    var pair = node.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_RELEASED, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                    new MouseActionEvent(MouseActionEvent.MOUSE_CLICKED, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                }
            }
        }
    }

    public void processScroll(double xOffset, double yOffset) {
        focused.forEach(node -> {
            var pair = node.relativePos(lastScreenX, lastScreenY);
            new ScrollEvent(ScrollEvent.ANY, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, xOffset, yOffset).fireEvent();
        });
    }

    public void processKey(KeyCode key, Modifiers modifier, boolean pressed) {
        focused.forEach(node ->
                new KeyEvent(pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, node, key, modifier, pressed).fireEvent());
    }

    public void processCharMods(char codePoint, Modifiers modifier) {
        focused.forEach(node ->
                new KeyEvent(KeyEvent.KEY_TYPED, node, KeyCode.KEY_UNDEFINED, String.valueOf(codePoint), modifier, true).fireEvent());
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
