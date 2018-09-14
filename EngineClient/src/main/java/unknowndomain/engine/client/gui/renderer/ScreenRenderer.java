package unknowndomain.engine.client.gui.renderer;

import unknowndomain.engine.client.gui.GuiScreen;

public class ScreenRenderer {

    private GuiScreen screen;

    public ScreenRenderer(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }
}
