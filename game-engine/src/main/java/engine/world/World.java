package engine.world;

import engine.block.Block;
import engine.component.GameObject;
import engine.game.Game;
import engine.math.BlockPos;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkManager;
import engine.world.hit.BlockHitResult;
import engine.world.hit.HitResult;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import static engine.world.chunk.ChunkConstants.*;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockGetter, BlockSetter, EntityManager, GameObject<World> {

    Game getGame();

    WorldProvider getProvider();

    Path getStoragePath();

    String getName();

    WorldCreationSetting getCreationSetting();

    WorldSetting getSetting();

    long getGameTick();

    BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance);

    BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);

    HitResult raycast(Vector3fc from, Vector3fc dir, float distance);

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.x() >> CHUNK_X_BITS, pos.y() >> CHUNK_Y_BITS, pos.z() >> CHUNK_Z_BITS);
    }

    void tick();

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Collection<Chunk> getLoadedChunks();

    void unload();

    boolean isUnloaded();

    void save();

    ChunkManager getChunkManager();

    boolean isLogicSide();
}
