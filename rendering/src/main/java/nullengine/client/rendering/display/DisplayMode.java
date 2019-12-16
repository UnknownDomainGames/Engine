package nullengine.client.rendering.display;

public enum DisplayMode {
    WINDOWED(false, true),
    FULLSCREEN(true, false),
    WINDOWED_FULLSCREEN(true,true);

    private final boolean isFullscreen;
    private final boolean isWindowed;

    DisplayMode(boolean isFullscreen, boolean isWindowed){
        this.isFullscreen = isFullscreen;
        this.isWindowed = isWindowed;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public boolean isWindowed() {
        return isWindowed;
    }
}
