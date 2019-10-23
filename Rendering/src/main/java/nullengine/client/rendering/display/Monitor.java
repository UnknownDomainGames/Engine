package nullengine.client.rendering.display;

import java.util.List;

public class Monitor {
    private final long pointer;
    private final String name;

    private final int physicsWidth;
    private final int physicsHeight;

    private final float scaleX;
    private final float scaleY;

    private final int posX;
    private final int posY;

    private final VideoMode videoMode;
    private final List<VideoMode> videoModes;

    public Monitor(long pointer, String name, int physicsWidth, int physicsHeight, float scaleX, float scaleY, int posX, int posY, VideoMode videoMode, List<VideoMode> videoModes) {
        this.pointer = pointer;
        this.name = name;
        this.physicsWidth = physicsWidth;
        this.physicsHeight = physicsHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.posX = posX;
        this.posY = posY;
        this.videoMode = videoMode;
        this.videoModes = videoModes;
    }

    public long getPointer() {
        return pointer;
    }

    public String getName() {
        return name;
    }

    public int getPhysicsWidth() {
        return physicsWidth;
    }

    public int getPhysicsHeight() {
        return physicsHeight;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public VideoMode getVideoMode() {
        return videoMode;
    }

    public List<VideoMode> getVideoModes() {
        return videoModes;
    }
}
