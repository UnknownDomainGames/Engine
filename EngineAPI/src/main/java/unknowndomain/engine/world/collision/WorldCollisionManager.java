package unknowndomain.engine.world.collision;

import org.joml.Vector3fc;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.Set;

public interface WorldCollisionManager {

    @Nonnull
    World getWorld();

    @Nonnull
    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance);

    @Nonnull
    RayTraceBlockHit raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);
}
