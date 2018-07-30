package unknowndomain.engine.client.world;

import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.unclassified.BlockObject;
import unknowndomain.engine.api.unclassified.World;
import unknowndomain.engine.api.world.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class FlatWorld implements World {
    private HashMap<BlockPos, BlockObject> blockData = new HashMap<>();

    public FlatWorld() {
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return null;
    }

    @Override
    public BlockObject getBlock(BlockPos pos) {
        return blockData.get(pos);
    }

    @Override
    public BlockObject setBlock(BlockPos pos, BlockObject block) {
        return blockData.put(pos, block);
    }


    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }
}
