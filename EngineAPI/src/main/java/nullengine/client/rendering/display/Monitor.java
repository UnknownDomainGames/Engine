package nullengine.client.rendering.display;

public class Monitor {
    private final long pointer;
    private final String name;

    private final int physicsWidth;
    private final int physicsHeight;

    private final float scaleX;
    private final float scaleY;

    private final int posX;
    private final int posY;

    private final int width;
    private final int height;
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int refreshRate;

    public Monitor(long pointer, String name, int physicsWidth, int physicsHeight, float scaleX, float scaleY, int posX, int posY, int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
        this.pointer = pointer;
        this.name = name;
        this.physicsWidth = physicsWidth;
        this.physicsHeight = physicsHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
        this.refreshRate = refreshRate;
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
