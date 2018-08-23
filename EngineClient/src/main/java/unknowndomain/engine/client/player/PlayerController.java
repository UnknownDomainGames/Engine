package unknowndomain.engine.client.player;

import unknowndomain.engine.action.Action;
import unknowndomain.engine.client.camera.CameraDefault;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.keybinding.Keybindings;
import unknowndomain.engine.entity.Player;

import java.util.List;

import org.lwjgl.glfw.GLFW;

public abstract class PlayerController {
    protected Camera camera = new CameraDefault();
    protected KeyBindingManager keyBindingManager;
    protected Player player;
    protected boolean paused = false;

    {
        keyBindingManager = new KeyBindingManager();
        Keybindings.INSTANCE.setup(keyBindingManager);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void tick() {
    }

    public abstract List<Action> getActions();

    public void handleCursorMove(double x, double y) {
        if (!paused)
            camera.rotate((float) x, (float) y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers) {
        switch (action) {
        case GLFW.GLFW_PRESS:
            keyBindingManager.handlePress(key, modifiers);
            break;
        case GLFW.GLFW_RELEASE:
            keyBindingManager.handleRelease(key, modifiers);
            break;
        default:
            break;
        }
        // if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
        // if (paused) {
        // window.hideCursor();
        // paused = false;
        // } else {
        // window.showCursor();
        // paused = true;
        // }
        // }
    }

    public void handleTextInput(int codepoint, int modifiers) {
    }

    public void handleMousePress(int button, int action, int modifiers) {
        switch (action) {
        case GLFW.GLFW_PRESS:
            keyBindingManager.handlePress(button + 400, modifiers);
            break;
        case GLFW.GLFW_RELEASE:
            keyBindingManager.handleRelease(button + 400, modifiers);
            break;
        default:
            break;
        }
    }

    public void handleScroll(double xoffset, double yoffset) {
        // renderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }
}
