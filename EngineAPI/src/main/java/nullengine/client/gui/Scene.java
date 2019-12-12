package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.Platform;
import nullengine.client.gui.event.old.CharEvent_;
import nullengine.client.gui.event.old.KeyEvent_;
import nullengine.client.gui.event.type.FocusEvent;
import nullengine.client.gui.event.type.KeyEvent;
import nullengine.client.gui.event.type.MouseEvent;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.client.rendering.display.callback.*;
import nullengine.event.Event;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void hookToEventBus() {
        Platform.getEngine().getEventBus().addListener(this::eventBusHook);
    }

    public void unhookFromEventBus() {
//        Platform.getEngine().getEventBus().
    }

    private void eventBusHook(Event event) {
        var root = this.root.get();
        root.handleEvent(event);
        root.getChildrenRecursive().forEach(component -> component.handleEvent(event));
    }

    public void update() {
        root.get().layout();
    }

    private double lastPosX = Double.NaN;
    private double lastPosY = Double.NaN;

    public final CursorCallback cursorCallback = (window, xPos, yPos) -> {
        var xCorr = xPos / window.getContentScaleX();
        var yCorr = yPos / window.getContentScaleY();
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
            var root = this.root.get();
            var olds = root.getPointingLastChildComponents((float) lastPosX, (float) lastPosY);
            var news = root.getPointingLastChildComponents((float) xCorr, (float) yCorr);
            var toRemove = new ArrayList<Node>();
            for (var i : news) {
                if (olds.contains(i)) {
                    new MouseEvent.MouseMoveEvent(i, lastPosX, lastPosY, xCorr, yCorr).fireEvent(i);
                    toRemove.add(i);
                } else {
                    new MouseEvent.MouseEnterEvent(i, lastPosX, lastPosY, xCorr, yCorr).fireEvent(i);
                }
            }
            olds.removeAll(toRemove);
            for (var i : olds) {
                new MouseEvent.MouseLeaveEvent(i, lastPosX, lastPosY, xCorr, yCorr).fireEvent(i);
            }
        }
        lastPosX = xCorr;
        lastPosY = yCorr;
    };

    public final MouseCallback mouseCallback = (window, button, action, modifiers) -> {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
            var root = this.root.get();
            var lastList = root.getPointingLastChildComponents((float) lastPosX, (float) lastPosY);
            if (action == GLFW.GLFW_PRESS) {
                for (var node : lastList) {
                    new FocusEvent.FocusGainEvent(node).fireEvent(node);
                }
                var a = root.getChildrenRecursive().stream().filter(component -> component.focused().get()).collect(Collectors.toList());
                var lastA = this.getLastChildNodeFromList(a);
                for (var node : lastA) {
                    new FocusEvent.FocusLostEvent(node).fireEvent(node);
                }
            }
            for (var node : lastList) {
                if (action == GLFW.GLFW_PRESS) {
                    var pair = node.relativePos(((float) lastPosX), ((float) lastPosY));
                    new MouseEvent.MouseClickEvent(node, pair.getLeft(), pair.getRight(), Key.valueOf(400 + button)).fireEvent(node);
                    ;
                }
                if (action == GLFW.GLFW_RELEASE) {
                    var pair = node.relativePos(((float) lastPosX), ((float) lastPosY));
                    new MouseEvent.MouseReleasedEvent(node, pair.getLeft(), pair.getRight(), Key.valueOf(400 + button)).fireEvent(node);
                    ;
                }
                if (action == GLFW.GLFW_REPEAT) {
                    var pair = node.relativePos(((float) lastPosX), ((float) lastPosY));
                    new MouseEvent.MouseHoldEvent(node, pair.getLeft(), pair.getRight(), Key.valueOf(400 + button)).fireEvent(node);
                    ;
                }
            }

        }
    };

    public final ScrollCallback scrollCallback = (window, xOffset, yOffset) -> {
        var root = this.root.get();
        var a = root.getChildrenRecursive().stream().filter(component -> component.focused().get()).collect(Collectors.toList());
        for (var node : a) {
            new MouseEvent.MouseWheelEvent(node, xOffset, yOffset).fireEvent(node);
        }

    };

    public final KeyCallback keyCallback = (window, key, scanCode, action, mods) -> {
        var root = this.root.get();
        var a = root.getChildrenRecursive().stream().filter(component -> component.focused().get()).collect(Collectors.toList());
        var lastA = getLastChildNodeFromList(a);
        for (Node node : lastA) {
            if (action == GLFW.GLFW_PRESS) {
                new KeyEvent.KeyDownEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)).fireEvent(node);
            } else if (action == GLFW.GLFW_REPEAT) {
                new KeyEvent.KeyHoldEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)).fireEvent(node);
            } else if (action == GLFW.GLFW_RELEASE) {
                new KeyEvent.KeyUpEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)).fireEvent(node);
            }
        }
    };

    public final CharCallback charCallback = (window, c) -> {
        root.get().getChildrenRecursive().stream().filter(component -> component.focused().get()).forEach(component -> component.handleEvent(new CharEvent_(component, c)));
    };

    private List<Node> getLastChildNodeFromList(List<Node> nodes) {
        var list = new ArrayList<>(nodes);
        var toRemove = new ArrayList<Node>();
        for (var i : list) {
            if (!i.parent().isEmpty())
                toRemove.add(i.parent().get());
        }
        list.removeAll(toRemove);
        return list;
    }
}
