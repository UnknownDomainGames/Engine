package engine.world.chunk;

import engine.world.World;
import engine.world.gen.Heightmap;

/**
 * Chunk Column
 * <p>
 * This object contains a column of chunks in a World object.
 * Chunk Column is a collection of chunks which have the same x- and z-coordinate
 */
public class ChunkColumn {

    private final World world;
    private final int chunkX;
    private final int chunkZ;

    private final Heightmap heightmap;

    public ChunkColumn(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        //TODO: see if we need more types of height map / postpone their initialization
        heightmap = Heightmap.create(this, Heightmap.NOT_AIR_PREDICATE);
    }

    public Chunk getChunk(int chunkY) {
        return world.getChunk(chunkX, chunkY, chunkZ, false);
    }

    public Heightmap getHeightmap() {
        return heightmap;
    }
}
