package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.input.*;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scene {

    private final MutableFloatValue width = new SimpleMutableFloatValue();
    private final MutableFloatValue height = new SimpleMutableFloatValue();

    private final MutableObjectValue<Parent> root = new SimpleMutableObjectValue<>();

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

    private float lastScreenX = Float.NaN;
    private float lastScreenY = Float.NaN;

    public void processCursor(double xPos, double yPos) {
        var screenX = (float) xPos;
        var screenY = (float) yPos;
        if (!Float.isNaN(lastScreenX) && !Float.isNaN(lastScreenY)) {
            var root = this.root.get();
            var lastNodes = root.getPointingComponents(lastScreenX, lastScreenY);
            var currentNodes = root.getPointingComponents(screenX, screenY);
            for (var target : currentNodes) {
                if (lastNodes.contains(target)) {
                    var pair = target.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_MOVED, target, target, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                } else {
                    var pair = target.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_ENTERED, target, target, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                }
            }
            lastNodes.removeAll(currentNodes);
            for (var i : lastNodes) {
                var pair = i.relativePos(screenX, screenY);
                new MouseEvent(MouseEvent.MOUSE_EXITED, i, i, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
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
            for (var target : nodes) {
                if (pressed) {
                    var pair = target.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_PRESSED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(target);
                } else {
                    var pair = target.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_RELEASED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(target);
                    new MouseActionEvent(MouseActionEvent.MOUSE_CLICKED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, button, modifier).fireEvent(target);
                }
            }
        }
    }

    public void processScroll(double xOffset, double yOffset) {
        focused.forEach(node -> {
            var pair = node.relativePos(lastScreenX, lastScreenY);
            new ScrollEvent(ScrollEvent.ANY, node, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, xOffset, yOffset).fireEvent();
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
}
