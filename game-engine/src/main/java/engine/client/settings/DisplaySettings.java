package engine.client.settings;

import configuration.Config;
import engine.graphics.GraphicsManager;
import engine.graphics.display.DisplayMode;
import engine.graphics.display.Window;

import java.util.Map;

public final class DisplaySettings {

    private DisplayMode displayMode = DisplayMode.WINDOWED;
    private int maxFps = -1;
    private int resolutionWidth = 854;
    private int resolutionHeight = 480;
    private int frameRate = 60;
    private float brightness = 1;
    private boolean vSync = false;
    private int uiScale = 100;
    private int hudScale = 100;

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

    public int getUiScale() {
        return uiScale;
    }

    public float getUiScalePercentage() {
        return uiScale / 100f;
    }

    public int getHudScale() {
        return hudScale;
    }

    public float getHudScalePercentage() {
        return hudScale / 100f;
    }

    public void setUiScale(int uiScale) {
        this.uiScale = uiScale;
    }

    public void setHudScale(int hudScale) {
        this.hudScale = hudScale;
    }

    public void load(Config config) {
        try {
            displayMode = DisplayMode.valueOf(config.getString("display_mode", "windowed").toUpperCase());
        } catch (IllegalArgumentException e) {
            displayMode = DisplayMode.WINDOWED;
        }
        resolutionWidth = config.getInt("res_width", 854);
        resolutionHeight = config.getInt("res_height", 480);
        frameRate = config.getInt("frame_rate", 60);
        uiScale = config.getInt("ui_scale", 100);
        hudScale = config.getInt("hud_scale", 100);
    }

    public Map<String, Object> save() {
        var config = new Config();
        config.set("display_mode", displayMode.name().toLowerCase());
        config.set("res_width", resolutionWidth);
        config.set("res_height", resolutionHeight);
        config.set("frame_rate", frameRate);
        config.set("ui_scale", uiScale);
        config.set("hud_scale", hudScale);
        return config.getRoot();
    }

    public void apply() {
        GraphicsManager graphicsManager = GraphicsManager.instance();
        Window window = graphicsManager.getWindow();
        window.setDisplayMode(displayMode, resolutionWidth, resolutionHeight, frameRate);
        float uiScalePercentage = getUiScalePercentage();
        graphicsManager.getGUIManager().setScale(uiScalePercentage, uiScalePercentage);
        float hudScalePercentage = getHudScalePercentage();
        graphicsManager.getHUDManager().setScale(hudScalePercentage, hudScalePercentage);
    }
}
