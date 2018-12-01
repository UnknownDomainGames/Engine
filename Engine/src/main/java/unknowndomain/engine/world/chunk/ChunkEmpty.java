package unknowndomain.engine.world.chunk;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChunkEmpty implements Chunk {

    public static final ChunkEmpty INSTANCE = new ChunkEmpty();

    private ChunkEmpty() {

    }

    @Nonnull
    @Override
    public List<Entity> getEntities() {
        return Collections.emptyList();
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return null; // TODO: Return air block.
    }

    @Override
    public Block setBlock(int x, int y, int z, Block destBlock) {
        throw new UnsupportedOperationException("Empty chunk cannot set block.");
    }

    @Override
    public World getWorld() {
        return null;
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
