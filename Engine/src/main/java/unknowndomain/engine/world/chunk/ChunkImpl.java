package unknowndomain.engine.world.chunk;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChunkImpl implements Chunk {

    private final World world;
    private final List<Entity> entities = new ArrayList<>();
    private final BlockStore[] blockStores = new BlockStore[16];

    public ChunkImpl(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int groupIndex = x >> 4;
        if (groupIndex >= blockStores.length) {
            return BlockAir.AIR;
        }

        BlockStore blockStore = blockStores[groupIndex];
        if (blockStore == null) {
            return BlockAir.AIR;
        }

        return blockStore.getBlock(x, y, z);
    }

    @NonNull
    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public Block setBlock(int x, int y, int z, Block destBlock) {
        int groupIndex = x >> 4;
        if (groupIndex >= blockStores.length) {
            // TODO: throw or increase.
            throw new IllegalArgumentException("Illegal height value of " + y);
        }

        BlockStore blockStore = blockStores[groupIndex];
        if (blockStore == null) {
            blockStore = new BlockStore(this);
            blockStores[groupIndex] = blockStore;
        }

        return blockStore.setBlock(x, y, z, destBlock);
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
