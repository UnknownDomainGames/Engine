package nullengine.world;

import nullengine.block.Block;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.raytrace.RayTraceBlockHit;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockGetter, BlockSetter, EntityManager {

    Game getGame();

    WorldProvider getProvider();

    Path getStoragePath();

    WorldCreationSetting getCreationSetting();

    WorldSetting getSetting();

    @Nonnull
    @Override
    default World getWorld() {
        return this;
    }

    long getGameTick();

    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance);

    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.x() >> 4, pos.y() >> 4, pos.z() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Collection<Chunk> getLoadedChunks();
}
