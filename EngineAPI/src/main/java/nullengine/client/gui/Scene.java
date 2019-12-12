package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.input.*;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.client.rendering.display.callback.*;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

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

    public final CursorCallback cursorCallback = (window, xPos, yPos) -> {
        if (!Float.isNaN(lastScreenX) && !Float.isNaN(lastScreenY)) {
            var root = this.root.get();
            var olds = root.getPointingComponents(lastScreenX, lastScreenY);
            var screenX = (float) xPos;
            var screenY = (float) yPos;
            var news = root.getPointingComponents(screenX, screenY);
            var toRemove = new ArrayList<Node>();
            for (var target : news) {
                if (olds.contains(target)) {
                    var pair = target.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_MOVED, target, target, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                    toRemove.add(target);
                } else {
                    var pair = target.relativePos(screenX, screenY);
                    new MouseEvent(MouseEvent.MOUSE_ENTERED, target, target, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
                }
            }
            olds.removeAll(toRemove);
            for (var i : olds) {
                var pair = i.relativePos(screenX, screenY);
                new MouseEvent(MouseEvent.MOUSE_EXITED, i, i, pair.getLeft(), pair.getRight(), screenX, screenY).fireEvent();
            }
        }
        lastScreenX = (float) xPos;
        lastScreenY = (float) yPos;
    };

    private final Set<Node> focused = new HashSet<>();

    public final MouseCallback mouseCallback = (window, button, action, mods) -> {
        if (!Float.isNaN(lastScreenX) && !Float.isNaN(lastScreenY)) {
            var root = this.root.get();
            var nodes = root.getPointingComponents(lastScreenX, lastScreenY);
            if (action == GLFW.GLFW_PRESS) {
                for (var node : nodes) {
                    if (node.disabled.get()) continue;
                    if (focused.contains(node)) continue;
                    node.focused.set(true);
                }
                List<Node> focusedList = new ArrayList<>(focused);
                focusedList.removeAll(nodes);
                focused.removeAll(focusedList);
                focusedList.forEach(node -> node.focused.set(false));
            }
            for (var target : nodes) {
                if (action == GLFW.GLFW_PRESS) {
                    var pair = target.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_PRESSED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, MouseButton.valueOf(button), KeyModifier.of(mods)).fireEvent(target);
                } else if (action == GLFW.GLFW_RELEASE) {
                    var pair = target.relativePos(lastScreenX, lastScreenY);
                    new MouseActionEvent(MouseActionEvent.MOUSE_RELEASED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, MouseButton.valueOf(button), KeyModifier.of(mods)).fireEvent(target);
                    new MouseActionEvent(MouseActionEvent.MOUSE_CLICKED, target, target, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, MouseButton.valueOf(button), KeyModifier.of(mods)).fireEvent(target);
                }
            }
        }
    };

    public final ScrollCallback scrollCallback = (window, xOffset, yOffset) -> focused.forEach(node -> {
        var pair = node.relativePos(lastScreenX, lastScreenY);
        new ScrollEvent(ScrollEvent.ANY, node, node, pair.getLeft(), pair.getRight(), lastScreenX, lastScreenY, xOffset, yOffset).fireEvent();
    });

    public final KeyCallback keyCallback = (window, key, scanCode, action, mods) -> focused.forEach(node -> {
        if (action == GLFW.GLFW_PRESS) {
            new KeyEvent(KeyEvent.KEY_PRESSED, node, KeyCode.valueOf(key), KeyModifier.of(mods), true).fireEvent();
        } else if (action == GLFW.GLFW_RELEASE) {
            new KeyEvent(KeyEvent.KEY_RELEASED, node, KeyCode.valueOf(key), KeyModifier.of(mods), false).fireEvent();
        }
    });

    public final CharModsCallback charModsCallback = (window, codepoint, mods) -> focused.forEach(node ->
            new KeyEvent(KeyEvent.KEY_TYPED, node, KeyCode.KEY_UNDEFINED, String.valueOf((char) codepoint), KeyModifier.of(mods), true).fireEvent());
}
