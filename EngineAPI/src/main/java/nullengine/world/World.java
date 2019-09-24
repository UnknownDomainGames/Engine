package nullengine.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.collision.RayTraceBlockHit;
import nullengine.world.raytrace.RayTraceEntityHit;
import org.joml.AABBd;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance);

    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);

    <T extends Entity> T spawnEntity(Class<T> entityType, double x, double y, double z);

    <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position);

    Entity spawnEntity(String providerName, double x, double y, double z);

    Entity spawnEntity(String providerName, Vector3dc position);

    void destroyEntity(Entity entity);

    List<Entity> getEntities();

    List<Entity> getEntities(Predicate<Entity> predicate);

    <T extends Entity> List<T> getEntitiesWithType(Class<T> entityType);

    List<Entity> getEntitiesWithBoundingBox(AABBd boundingBox);

    List<Entity> getEntitiesWithBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    List<Entity> getEntitiesWithSphere(double centerX, double centerY, double centerZ, double radius);

    RayTraceEntityHit raycastEntity(Vector3fc from, Vector3fc dir, float distance);

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.x() >> 4, pos.y() >> 4, pos.z() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Collection<Chunk> getLoadedChunks();
}
