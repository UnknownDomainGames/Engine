package nullengine.client.gui.internal;

public abstract class GUIPlatform {

    private static GUIPlatform PLATFORM;

    public static GUIPlatform getInstance() {
        return PLATFORM;
    }

    public abstract SceneHelper getSceneHelper();
}
