package engine.graphics.graph;

public final class Frame {

    private final int number;
    private final long startTime;
    private final float timeLastFrame;
    private final float tickLastFrame;

    private final int width;
    private final int height;
    private final boolean resized;

    public Frame(int number, long startTime, float timeLastFrame, int width, int height, boolean resized) {
        this.number = number;
        this.startTime = startTime;
        this.timeLastFrame = timeLastFrame;
        this.tickLastFrame = timeLastFrame;
        this.width = width;
        this.height = height;
        this.resized = resized;
    }

    public Frame(int number, long startTime, float timeLastFrame, float tickLastFrame, int width, int height, boolean resized) {
        this.number = number;
        this.startTime = startTime;
        this.timeLastFrame = timeLastFrame;
        this.tickLastFrame = tickLastFrame;
        this.width = width;
        this.height = height;
        this.resized = resized;
    }

    public int getNumber() {
        return number;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getTimeLastFrame() {
        return timeLastFrame;
    }

    public float getTickLastFrame() {
        return tickLastFrame;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }
}
