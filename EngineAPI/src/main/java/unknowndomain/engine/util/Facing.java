package unknowndomain.engine.util;

public enum Facing {
    NORTH(0, 1, 0, 0, 1),
    SOUTH(1, 0, 0, 0, -1),
    EAST(2, 3, 1, 0, 0),
    WEST(3, 2, -1, 0, 0),
    UP(4, 5, 0, 1, 0),
    DOWN(5, 4, 0, -1, 0);

    private final int index;
    private final int opposite;
    public final int offsetX, offsetY, offsetZ;

    Facing(int index, int opposite, int offsetX, int offsetY, int offsetZ) {
        this.index = index;
        this.opposite = opposite;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public int getIndex() {
        return index;
    }

    public Facing opposite() {
        return values()[opposite];
    }
}
