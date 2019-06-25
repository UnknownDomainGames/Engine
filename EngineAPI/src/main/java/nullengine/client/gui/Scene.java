package nullengine.client.gui;

import com.github.mouse0w0.lib4j.observable.value.MutableIntValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableIntValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableIntValue;
import nullengine.client.gui.event.CharEvent;
import nullengine.client.gui.event.FocusEvent;
import nullengine.client.gui.event.KeyEvent;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.client.rendering.display.Window;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Scene {

    private final MutableIntValue width = new SimpleMutableIntValue();
    private final MutableIntValue height = new SimpleMutableIntValue();

    private Container root;

    public Scene(Container root) {
        setRoot(root);
    }

    public ObservableIntValue width() {
        return width.toImmutable();
    }

    public int getWidth() {
        return width.get();
    }

    public ObservableIntValue height() {
        return height.toImmutable();
    }

    public int getHeight() {
        return height.get();
    }

    public void setSize(int width, int height) {
        this.width.set(width);
        this.height.set(height);
        updateRoot();
    }

    public Container getRoot() {
        return root;
    }

    public void setRoot(Container root) {
        this.root = Objects.requireNonNull(root);
        updateRoot();
    }

    private void updateRoot() {
        this.root.width.set(getWidth() - root.x().get());
        this.root.height.set(getHeight() - root.y().get());
        this.root.needsLayout();
    }

    public void update() {
        root.layout();
    }

    private double lastPosX = Double.NaN;
    private double lastPosY = Double.NaN;

    public final Window.CursorCallback cursorCallback = (window, xPos, yPos) -> {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
            var old = root.getPointingComponents((float) lastPosX, (float) lastPosY);
            var n = root.getPointingComponents((float) xPos, (float) yPos);
            List<Component> moveEvent = old.stream().filter(n::contains).collect(Collectors.toList());
            List<Component> leaveEvent = old.stream().filter(o -> !moveEvent.contains(o)).collect(Collectors.toList());
            List<Component> enterEvent = n.stream().filter(o -> !moveEvent.contains(o)).collect(Collectors.toList());
            moveEvent.addAll(root.getChildrenRecursive().stream().filter(c -> c.focused.get()).collect(Collectors.toList()));
            moveEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseMoveEvent(component, lastPosX, lastPosY, xPos, yPos)));
            enterEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseEnterEvent(component, lastPosX, lastPosY, xPos, yPos)));
            leaveEvent.forEach(component -> component.handleEvent(new MouseEvent.MouseLeaveEvent(component, lastPosX, lastPosY, xPos, yPos)));
        }
        lastPosX = xPos;
        lastPosY = yPos;
    };

    public final Window.MouseCallback mouseCallback = (window, button, action, modifiers) -> {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
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

    public final Window.ScrollCallback scrollCallback = (window, xOffset, yOffset) -> {

    };

    public final Window.KeyCallback keyCallback = (window, key, scanCode, action, mods) -> {
        var a = root.getUnmodifiableChildren().stream().filter(component -> component.focused().get()).collect(Collectors.toList());
        a.add(root);
        for (Component component : a) {
            if (action == GLFW.GLFW_PRESS) {
                component.handleEvent(new KeyEvent.KeyDownEvent(component, Key.valueOf(key), ActionMode.PRESS, KeyModifier.valueOf(mods)));
            } else if (action == GLFW.GLFW_REPEAT) {
                component.handleEvent(new KeyEvent.KeyHoldEvent(component, Key.valueOf(key), ActionMode.PRESS, KeyModifier.valueOf(mods)));
            } else if (action == GLFW.GLFW_RELEASE) {
                component.handleEvent(new KeyEvent.KeyUpEvent(component, Key.valueOf(key), ActionMode.PRESS, KeyModifier.valueOf(mods)));
            }
        }
    };

    public final Window.CharCallback charCallback = (window, c) -> {
        root.getUnmodifiableChildren().stream().filter(component -> component.focused().get()).forEach(component -> component.handleEvent(new CharEvent(component, c)));
    };
}
