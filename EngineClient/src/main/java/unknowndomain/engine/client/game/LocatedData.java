package unknowndomain.engine.client.game;

import unknowndomain.engine.math.BlockPos;

public class LocatedData<T> {
    public final BlockPos pos;
    public final T value;

    public LocatedData(BlockPos pos, T value) {
        this.pos = pos;
        this.value = value;
    }

    public LocatedData(LocatedData<T> data) {
        this.pos = data.pos;
        this.value = data.value;
    }
}
