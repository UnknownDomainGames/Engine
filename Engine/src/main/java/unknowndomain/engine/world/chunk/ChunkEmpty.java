package unknowndomain.engine.world.chunk;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChunkEmpty implements Chunk {

    public static final ChunkEmpty INSTANCE = new ChunkEmpty();

    private ChunkEmpty() {
    }

    @Nonnull
    @Override
    public List<Entity> getEntities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return BlockAir.AIR;
    }

    @Override
    public Block setBlock(int x, int y, int z, Block block) {
        throw new UnsupportedOperationException("Empty chunk cannot set block.");
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        throw new UnsupportedOperationException();
    }
}
