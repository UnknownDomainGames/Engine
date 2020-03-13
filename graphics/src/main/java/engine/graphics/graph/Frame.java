package engine.graphics.graph;

public final class Frame {

    private final int number;
    private final long startTime;
    private final float timeToLastDraw;
    private final float timeToLastUpdate;

    private final int width;
    private final int height;
    private final boolean resized;

    public Frame(int number, long startTime, float timeToLastDraw, float timeToLastUpdate, int width, int height, boolean resized) {
        this.number = number;
        this.startTime = startTime;
        this.timeToLastDraw = timeToLastDraw;
        this.timeToLastUpdate = timeToLastUpdate;
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

    public float getTimeToLastDraw() {
        return timeToLastDraw;
    }

    public float getTimeToLastUpdate() {
        return timeToLastUpdate;
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
