package engine.graphics.graph;

public final class Frame {

    private final int number;
    private final long startTime;
    private final float timeToLastDraw;
    private final float timeToLastUpdate;

    private final int outputWidth;
    private final int outputHeight;
    private final boolean resized;

    public Frame(int number, long startTime, float timeToLastDraw, float timeToLastUpdate,
                 int outputWidth, int outputHeight, boolean resized) {
        this.number = number;
        this.startTime = startTime;
        this.timeToLastDraw = timeToLastDraw;
        this.timeToLastUpdate = timeToLastUpdate;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
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

    public int getOutputWidth() {
        return outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public boolean isResized() {
        return resized;
    }
}
