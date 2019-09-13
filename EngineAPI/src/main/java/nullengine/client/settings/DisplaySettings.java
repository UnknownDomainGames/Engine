package nullengine.client.settings;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.display.DisplayMode;
import nullengine.client.rendering.display.Window;

public final class DisplaySettings {

    private DisplayMode displayMode = DisplayMode.WINDOWED;
    private int maxFps = -1;
    private int resolutionWidth = -1;
    private int resolutionHeight = -1;
    private float brightness = 1;
    private boolean vSync = false;

    public void apply() {
        Window window = RenderManager.instance().getWindow();
        window.setDisplayMode(displayMode);
    }
}
