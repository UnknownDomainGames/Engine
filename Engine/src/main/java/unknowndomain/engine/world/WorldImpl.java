package unknowndomain.engine.world;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.unclassified.ChunkImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;

public class WorldImpl implements World {
    private final Game context;

    private Table<Integer, Integer, ChunkImpl> chunkTable = HashBasedTable.create(128, 128);
    private ExecutorService executor;

    public WorldImpl(Game context) {
        this.context = context;
    }

//    @Override
//    public <Context extends RuntimeObject> Prototype<? extends RuntimeObject, Context> getPrototype() {
//        return null;
//    }

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

    @Override
    public Chunk getChunk(int x, int z) {
        return chunkTable.get(x, z);
    }

    @Override
    public BlockObject getBlock(BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        return chunkTable.get(chunkPos.getChunkX(), chunkPos.getChunkZ()).getBlock(pos);
    }

    @Override
    public BlockObject setBlock(BlockPos pos, BlockObject block) {
        ChunkPos chunkPos = pos.toChunk();
        return null;
    }

    public void tick() {
//        for (ChunkImpl chunk : chunkTable.values()) {
//            executor.execute(chunk::tick);
//        }
    }
}
