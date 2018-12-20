package unknowndomain.engine.world.chunk;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
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
}
