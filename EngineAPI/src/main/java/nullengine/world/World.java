package nullengine.world;

import nullengine.entity.Entity;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.collision.WorldCollisionManager;
import org.joml.Vector3dc;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockGetter, BlockSetter {

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

    WorldCollisionManager getCollisionManager();

    WorldEntityManager getEntityManager();

    <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position);

    Entity spawnEntity(String provider, Vector3dc position);

    Collection<Entity> getEntities();

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.x() >> 4, pos.y() >> 4, pos.z() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Collection<Chunk> getLoadedChunks();
}
