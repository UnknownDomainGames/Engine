package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.Platform;
import nullengine.client.gui.event.CharEvent;
import nullengine.client.gui.event.FocusEvent;
import nullengine.client.gui.event.KeyEvent;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.client.rendering.display.callback.*;
import nullengine.event.Event;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

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
            var old = root.getPointingComponents((float) lastPosX, (float) lastPosY);
            var n = root.getPointingComponents((float) xCorr, (float) yCorr);
            List<Node> moveEvent = old.stream().filter(n::contains).collect(Collectors.toList());
            List<Node> leaveEvent = old.stream().filter(o -> !moveEvent.contains(o)).collect(Collectors.toList());
            List<Node> enterEvent = n.stream().filter(o -> !moveEvent.contains(o)).collect(Collectors.toList());
            moveEvent.addAll(root.getChildrenRecursive().stream().filter(c -> c.focused.get()).collect(Collectors.toList()));
            moveEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseMoveEvent(component, lastPosX, lastPosY, xCorr, yCorr)));
            enterEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseEnterEvent(component, lastPosX, lastPosY, xCorr, yCorr)));
            leaveEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseLeaveEvent(component, lastPosX, lastPosY, xCorr, yCorr)));
        }
        lastPosX = xCorr;
        lastPosY = yCorr;
    };

    public final MouseCallback mouseCallback = (window, button, action, modifiers) -> {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
            var root = this.root.get();
            var list = root.getPointingComponents((float) lastPosX, (float) lastPosY);
            if (action == GLFW.GLFW_PRESS) {
                root.getUnmodifiableChildren().stream().filter(c -> c.focused.get()).forEach(component -> component.handleEvent(new FocusEvent.FocusLostEvent(component)));
                list.forEach(component -> {
                    component.handleEvent(new FocusEvent.FocusGainEvent(component));
                    var pair = component.relativePos(((float) lastPosX), ((float) lastPosY));
                    component.handleEvent(new MouseEvent.MouseClickEvent(component, pair.getLeft(), pair.getRight(), Key.valueOf(400 + button)));
                });
            }
            if (action == GLFW.GLFW_RELEASE) {
                list.addAll(root.getChildrenRecursive().stream().filter(c -> c.focused.get()).collect(Collectors.toList()));
                list.forEach(component -> component.handleEvent(new MouseEvent.MouseReleasedEvent(component, (float) lastPosX, (float) lastPosY, Key.valueOf(400 + button))));
            }
            if (action == GLFW.GLFW_REPEAT) {
                list.forEach(component -> component.handleEvent(new MouseEvent.MouseHoldEvent(component, (float) lastPosX, (float) lastPosY, Key.valueOf(400 + button))));
            }
        }
    };

    public final ScrollCallback scrollCallback = (window, xOffset, yOffset) -> {

    };

    public final KeyCallback keyCallback = (window, key, scanCode, action, mods) -> {
        var root = this.root.get();
        var a = root.getChildrenRecursive().stream().filter(component -> component.focused().get()).collect(Collectors.toList());
        a.add(root);
        for (Node node : a) {
            if (action == GLFW.GLFW_PRESS) {
                node.handleEvent(new KeyEvent.KeyDownEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            } else if (action == GLFW.GLFW_REPEAT) {
                node.handleEvent(new KeyEvent.KeyHoldEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            } else if (action == GLFW.GLFW_RELEASE) {
                node.handleEvent(new KeyEvent.KeyUpEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            }
        }
    };

    public final CharCallback charCallback = (window, c) -> {
        root.get().getChildrenRecursive().stream().filter(component -> component.focused().get()).forEach(component -> component.handleEvent(new CharEvent(component, c)));
    };
}
