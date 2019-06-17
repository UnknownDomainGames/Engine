package unknowndomain.engine.world.chunk;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;
import unknowndomain.game.init.Blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static unknowndomain.engine.world.chunk.ChunkConstants.*;

public class BlankChunk implements Chunk {

    private final World world;
    private final int chunkX;
    private final int chunkY;
    private final int chunkZ;

    private final Vector3fc min;
    private final Vector3fc max;

    private final List<Entity> entities = new ArrayList<>();

    public BlankChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.min = new Vector3f(chunkX << BITS_X, chunkY << BITS_Y, chunkZ << BITS_Z);
        this.max = min.add(16, 16, 16, new Vector3f());
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkY() {
        return chunkY;
    }

    @Override
    public int getChunkZ() {
        return chunkZ;
    }

    @Nonnull
    @Override
    public Vector3fc getMin() {
        return min;
    }

    @Nonnull
    @Override
    public Vector3fc getMax() {
        return max;
    }


    @Nonnull
    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return Blocks.AIR;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return Blocks.AIR;
    }

    @Override
    public boolean isAirChunk() {
        return true;
    }
}
