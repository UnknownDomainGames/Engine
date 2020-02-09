package engine.graphics.display;

public final class VideoMode {
    private final int width;
    private final int height;
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int refreshRate;

    public VideoMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
        this.width = width;
        this.height = height;
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
        this.refreshRate = refreshRate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRedBits() {
        return redBits;
    }

    public int getGreenBits() {
        return greenBits;
    }

    public int getBlueBits() {
        return blueBits;
    }

    public int getRefreshRate() {
        return refreshRate;
    }
}
