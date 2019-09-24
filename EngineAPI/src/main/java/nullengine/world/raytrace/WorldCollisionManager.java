package nullengine.world.raytrace;

import nullengine.block.Block;
import nullengine.world.World;
import org.joml.Vector3fc;

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
