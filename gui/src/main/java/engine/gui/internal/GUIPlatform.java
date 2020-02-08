package engine.gui.internal;

public abstract class GUIPlatform {

    private static GUIPlatform PLATFORM;

    public static GUIPlatform getInstance() {
        return PLATFORM;
    }

    protected static void setInstance(GUIPlatform platform) {
        PLATFORM = platform;
    }

    public abstract StageHelper getStageHelper();

    public abstract SceneHelper getSceneHelper();

    public abstract ClipboardHelper getClipboardHelper();
}
