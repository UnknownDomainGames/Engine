package unknowndomain.engine.util;

import unknowndomain.engine.math.BlockPos;

public enum Facing {
    NORTH(1, 0, 0, 1),
    SOUTH(0, 0, 0, -1),
    EAST(3, 1, 0, 0),
    WEST(2, -1, 0, 0),
    TOP(5, 0, 1, 0),
    BOTTOM(4, 0, -1, 0);

    private final int opposite;
    public final int offsetX, offsetY, offsetZ;

    Facing(int opposite, int offsetX, int offsetY, int offsetZ) {
        this.opposite = opposite;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public Facing opposite() {
        return values()[opposite];
    }

    public BlockPos offset(BlockPos pos) {
        return pos.add(offsetX, offsetY, offsetZ);
    }
}
