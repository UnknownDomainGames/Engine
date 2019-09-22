package nullengine.client.settings;

import configuration.Config;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.display.DisplayMode;
import nullengine.client.rendering.display.Window;

import java.util.Map;

public final class DisplaySettings {

    private DisplayMode displayMode = DisplayMode.WINDOWED;
    private int maxFps = -1;
    private int resolutionWidth = -1;
    private int resolutionHeight = -1;
    private int frameRate = 60;
    private float brightness = 1;
    private boolean vSync = false;

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void load(Config config){
        try {
            displayMode = DisplayMode.valueOf(config.getString("display_mode", "windowed").toUpperCase());
        }
        catch (IllegalArgumentException e){
            displayMode = DisplayMode.WINDOWED;
        }
        resolutionWidth = config.getInt("res_width");
        resolutionHeight = config.getInt("res_height");
        frameRate = config.getInt("frame_rate", 60);
    }

    public Map<String, Object> save(){
        var config = new Config();
        config.set("display_mode", displayMode.name().toLowerCase());
        config.set("res_width", resolutionWidth);
        config.set("res_height", resolutionHeight);
        config.set("frame_rate", frameRate);
        return config.getRoot();
    }

    public void apply() {
        Window window = RenderManager.instance().getWindow();
        window.setDisplayMode(displayMode, resolutionWidth, resolutionHeight, frameRate);
    }
}
