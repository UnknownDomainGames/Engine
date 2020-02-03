package nullengine.client.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.*;
import nullengine.client.gui.input.KeyEvent;
import nullengine.client.gui.input.MouseActionEvent;
import nullengine.client.gui.input.MouseEvent;
import nullengine.client.gui.input.ScrollEvent;
import nullengine.client.gui.misc.Pos;
import nullengine.input.KeyCode;
import nullengine.input.Modifiers;
import nullengine.input.MouseButton;
import nullengine.util.Color;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;

public class Scene implements EventTarget {

    private final MutableIntValue viewportWidth = new SimpleMutableIntValue();
    private final MutableIntValue viewportHeight = new SimpleMutableIntValue();

    private final MutableFloatValue width = new SimpleMutableFloatValue();
    private final MutableFloatValue height = new SimpleMutableFloatValue();

    private final MutableFloatValue scaleX = new SimpleMutableFloatValue(1);
    private final MutableFloatValue scaleY = new SimpleMutableFloatValue(1);

    final MutableObjectValue<Stage> stage = new SimpleMutableObjectValue<>();

    private final MutableObjectValue<Color> fill = new SimpleMutableObjectValue<>(Color.BLACK);

    private final MutableObjectValue<Parent> root = new SimpleMutableObjectValue<>();

    private final EventHandlerManager eventHandlerManager = new EventHandlerManager();

    public Scene(Parent root) {
        setRoot(root);
    }

    public ObservableIntValue viewportWidth() {
        return viewportWidth.toUnmodifiable();
    }

    public int getViewportWidth() {
        return viewportWidth.get();
    }

    public ObservableIntValue viewportHeight() {
        return viewportHeight.toUnmodifiable();
    }

    public int getViewportHeight() {
        return viewportHeight.get();
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

    public void setViewport(int width, int height) {
        setViewport(width, height, 1f, 1f);
    }

    public void setViewport(int width, int height, float scaleX, float scaleY) {
        this.viewportWidth.set(width);
        this.viewportHeight.set(height);
        this.scaleX.set(scaleX);
        this.scaleY.set(scaleY);
        this.width.set(width / scaleX);
        this.height.set(height / scaleY);
        updateRoot();
    }

    public float getScaleX() {
        return scaleX.get();
    }

    public float getScaleY() {
        return scaleY.get();
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
        for (Popup popup : popups) {
            popup.layout();
        }
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
                    var pos = node.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_MOVED, node, pos.getX(), pos.getY(), screenX, screenY).fireEvent();
                } else {
                    var pos = node.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_ENTERED, node, pos.getX(), pos.getY(), screenX, screenY).fireEvent();
                }
            }
            lastNodes.removeAll(currentNodes);
            for (var node : lastNodes) {
                var pos = node.relativePos(screenX, screenY);
                new MouseEvent(MouseEvent.MOUSE_EXITED, node, pos.getX(), pos.getY(), screenX, screenY).fireEvent();
            }
            if (lastNodes.isEmpty() && currentNodes.isEmpty()) {
                new MouseEvent(MouseEvent.MOUSE_MOVED, this, screenX, screenY, screenX, screenY).fireEvent();
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
                    var pos = node.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_PRESSED, node, pos.getX(), pos.getY(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                } else {
                    var pos = node.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_RELEASED, node, pos.getX(), pos.getY(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                    new MouseActionEvent(MouseActionEvent.MOUSE_CLICKED, node, pos.getX(), pos.getY(), lastScreenX, lastScreenY, button, modifier).fireEvent(node);
                }
            }
        }
    }

    public void processScroll(double xOffset, double yOffset) {
        focused.forEach(node -> {
            var pos = node.relativePos(lastScreenX, lastScreenY);
            new ScrollEvent(ScrollEvent.ANY, node, pos.getX(), pos.getY(), lastScreenX, lastScreenY, xOffset, yOffset).fireEvent();
        });
    }

    public void processKey(KeyCode key, Modifiers modifier, boolean pressed) {
        focused.forEach(node ->
                new KeyEvent(pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, node, key, modifier, pressed).fireEvent());
    }

    public void processCharMods(char codePoint, Modifiers modifier) {
        focused.forEach(node ->
                new KeyEvent(KeyEvent.KEY_TYPED, node, KeyCode.NONE, String.valueOf(codePoint), modifier, true).fireEvent());
    }

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    private ObservableList<Popup> popups = ObservableCollections.observableList(new LinkedList<>());
    private ObservableList<Popup> unmodifiablePopups = ObservableCollections.unmodifiableObservableList(popups);

    public ObservableList<Popup> getPopups() {
        return unmodifiablePopups;
    }

    public void showPopup(@Nonnull Popup popup, boolean shouldFollowCursor, Pos location) {
        if (shouldFollowCursor) {
            EventHandler<MouseEvent> handler = event -> {
                float x = 0, y = 0;
                switch (location.getHpos()) {
                    case LEFT:
                        x = event.getScreenX();
                        break;
                    case CENTER:
                        x = event.getScreenX() - popup.prefWidth() / 2;
                        break;
                    case RIGHT:
                        x = event.getScreenX() - popup.prefWidth();
                        break;
                }
                switch (location.getVpos()) {
                    case TOP:
                    case BASELINE:
                        y = event.getScreenY();
                        break;
                    case CENTER:
                        y = event.getScreenY() - popup.prefHeight() / 2;
                        break;
                    case BOTTOM:
                        y = event.getScreenY() - popup.prefHeight();
                        break;
                }
                popup.relocate(x, y);
            };
            addEventHandler(MouseEvent.MOUSE_MOVED, handler);
            popup.getInsertedHandlers().add(new ImmutablePair<>(MouseEvent.MOUSE_MOVED, handler));
            popup.relocate(lastScreenX, lastScreenY);
        }
        popups.add(popup);
    }

    public void closePopup(Popup popup) {
        for (Pair<EventType, EventHandler> insertedHandler : popup.getInsertedHandlers()) {
            removeEventHandler(insertedHandler.getKey(), insertedHandler.getValue());
        }
        popups.remove(popup);
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
