package unknowndomain.engine.world;

import org.joml.Vector3fc;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.component.RuntimeObject;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.chunk.Chunk;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * World instance, should spawn by {@link unknowndomain.engine.game.Game}
 */
public interface World extends RuntimeObject, BlockAccessor {

    Game getGame();

    List<Entity> getEntities();

    RayTraceBlockHit raycast(Vector3fc from, Vector3fc dir, float distance);

    RayTraceBlockHit raycast(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    @Nonnull
    @Override
    default World getWorld() {
        return this;
    }

    interface Config {

    }
}
