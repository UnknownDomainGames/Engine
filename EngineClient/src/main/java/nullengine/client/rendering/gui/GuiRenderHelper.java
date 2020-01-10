package nullengine.client.rendering.gui;

import nullengine.client.gui.GuiManager;
import nullengine.client.gui.Scene;
import nullengine.client.gui.internal.impl.gl.GLGUIRenderer;

/**
 * render for any gui
 */
public class GuiRenderHelper {

    private final GuiManager manager;
    private final GLGUIRenderer renderer;

    public GuiRenderHelper(GuiManager manager) {
        this.manager = manager;
        this.renderer = new GLGUIRenderer();
    }

    public void render() {
        if (manager.isHudVisible() && !manager.isDisplayingScreen()) {
            for (Scene scene : manager.getDisplayingHuds().values()) {
                renderer.render(scene);
            }
        }

        if (manager.isDisplayingScreen()) {
            renderer.render(manager.getDisplayingScreen());
        }
    }

    public void dispose() {
        renderer.dispose();
    }
}
