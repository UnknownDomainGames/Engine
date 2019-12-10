package nullengine.client.gui;

import com.github.mouse0w0.observable.value.*;
import nullengine.Platform;
import nullengine.client.gui.event.type.MouseEvent;
import nullengine.client.gui.event.old.CharEvent_;
import nullengine.client.gui.event.old.FocusEvent_;
import nullengine.client.gui.event.old.KeyEvent_;
import nullengine.client.gui.event.old.MouseEvent_;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.client.rendering.display.callback.*;
import nullengine.event.Event;
import org.apache.commons.lang3.Validate;
import org.lwjgl.glfw.GLFW;

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
            var olds = root.getPointingComponents((float) lastPosX, (float) lastPosY);
            var news = root.getPointingComponents((float) xCorr, (float) yCorr);
            Node old = olds.size() != 0? olds.get(olds.size() - 1):null;
            Node n = news.size() != 0? news.get(news.size() - 1):null;
            if (n!=old){
                if (old!=null)
                    new MouseEvent.MouseLeaveEvent(old,lastPosX,lastPosY,xCorr,yCorr).fireEvent(old);
                if (n!=null)
                    new MouseEvent.MouseEnterEvent(n,lastPosX,lastPosY,xCorr,yCorr).fireEvent(n);
            }
            if (n!=null)
                new MouseEvent.MouseMoveEvent(n,lastPosX,lastPosY,xCorr,yCorr).fireEvent(n);
            if (old!=null)
                new MouseEvent.MouseMoveEvent(old,lastPosX,lastPosY,xCorr,yCorr).fireEvent(old);
        }
        lastPosX = xCorr;
        lastPosY = yCorr;
    };

    public final MouseCallback mouseCallback = (window, button, action, modifiers) -> {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)) {
            var root = this.root.get();
            var list = root.getPointingComponents((float) lastPosX, (float) lastPosY);
            if (action == GLFW.GLFW_PRESS) {
                root.getUnmodifiableChildren().stream().filter(c -> c.focused.get()).forEach(component -> component.handleEvent(new FocusEvent_.FocusLostEvent(component)));
                if (list.size() != 0) {
                    var node = list.get(list.size() - 1);
                    var pair = node.relativePos(((float) lastPosX), ((float) lastPosY));
                    var event = new MouseEvent.MouseClickEvent(node, pair.getLeft(), pair.getRight(), Key.valueOf(400 + button));
                    event.fireEvent(node);
                }
            }
            if (action == GLFW.GLFW_RELEASE) {
                list.addAll(root.getChildrenRecursive().stream().filter(c -> c.focused.get()).collect(Collectors.toList()));
                list.forEach(component -> component.handleEvent(new MouseEvent_.MouseReleasedEvent(component, (float) lastPosX, (float) lastPosY, Key.valueOf(400 + button))));
            }
            if (action == GLFW.GLFW_REPEAT) {
                list.forEach(component -> component.handleEvent(new MouseEvent_.MouseHoldEvent(component, (float) lastPosX, (float) lastPosY, Key.valueOf(400 + button))));
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
                node.handleEvent(new KeyEvent_.KeyDownEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            } else if (action == GLFW.GLFW_REPEAT) {
                node.handleEvent(new KeyEvent_.KeyHoldEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            } else if (action == GLFW.GLFW_RELEASE) {
                node.handleEvent(new KeyEvent_.KeyUpEvent(node, Key.valueOf(key), ActionMode.PRESS, KeyModifier.of(mods)));
            }
        }
    };

    public final CharCallback charCallback = (window, c) -> {
        root.get().getChildrenRecursive().stream().filter(component -> component.focused().get()).forEach(component -> component.handleEvent(new CharEvent_(component, c)));
    };
}
