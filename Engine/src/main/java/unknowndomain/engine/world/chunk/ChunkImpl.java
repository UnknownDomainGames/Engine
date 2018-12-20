package unknowndomain.engine.world.chunk;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkImpl implements Chunk {

    private final World world;
    private final List<Entity> entities = new ArrayList<>();
    private BlockStorage blockStorage;

    public ChunkImpl(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if (blockStorage == null) {
            return BlockAir.AIR;
        }

        return blockStorage.getBlock(x, y, z);
    }

    @NonNull
    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public Block setBlock(int x, int y, int z, Block block) {
        if (blockStorage == null) {
            blockStorage = new BlockStorage(this);
        }

        return blockStorage.setBlock(x, y, z, block);
    }
}
