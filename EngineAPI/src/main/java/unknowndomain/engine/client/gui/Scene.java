package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.value.MutableIntValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableIntValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableIntValue;
import org.lwjgl.glfw.GLFW;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.rendering.display.GameWindow;

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

    public final GameWindow.CursorCallback cursorCallback = (xpos, ypos) -> {
        if(!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)){
            var old = root.getPointingComponents((float)lastPosX,(float)lastPosY);
            var n = root.getPointingComponents((float)xpos,(float)ypos);
            List<Component> moveevent = old.stream().filter(n::contains).collect(Collectors.toList());
            List<Component> leaveevent = old.stream().filter(o -> !moveevent.contains(o)).collect(Collectors.toList());
            List<Component> enterevent = n.stream().filter(o -> !moveevent.contains(o)).collect(Collectors.toList());
            moveevent.forEach(component -> component.handleEvent(new MouseEvent.MouseMoveEvent(lastPosX,lastPosY,xpos,ypos)));
            enterevent.forEach(component -> component.handleEvent(new MouseEvent.MouseEnterEvent(lastPosX,lastPosY,xpos,ypos)));
            leaveevent.forEach(component -> component.handleEvent(new MouseEvent.MouseLeaveEvent(lastPosX,lastPosY,xpos,ypos)));
        }
        lastPosX = xpos;
        lastPosY = ypos;
    };

    public final GameWindow.MouseCallback mouseCallback = (button, action, modifiers) -> {
        if(!Double.isNaN(lastPosX) && !Double.isNaN(lastPosY)){
            var list = root.getPointingComponents((float)lastPosX,(float)lastPosY);
            if(action == GLFW.GLFW_PRESS)
                list.forEach(component -> component.handleEvent(new MouseEvent.MouseClickEvent((float)lastPosX,(float)lastPosY, Key.valueOf(400 + button))));
            if(action == GLFW.GLFW_RELEASE)
                list.forEach(component -> component.handleEvent(new MouseEvent.MouseReleasedEvent((float)lastPosX,(float)lastPosY, Key.valueOf(400 + button))));
        }
    };

    public final GameWindow.ScrollCallback scrollCallback = (xoffset, yoffset) -> {

    };

    public final GameWindow.KeyCallback keyCallback = (key, scancode, action, mods) -> {

    };

    public final GameWindow.CharCallback charCallback = c -> {

    };
}
