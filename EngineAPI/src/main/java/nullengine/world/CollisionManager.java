package nullengine.world;

import nullengine.block.Block;
import nullengine.world.hit.BlockHitResult;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.Set;

public interface CollisionManager {

    @Nonnull
    BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance);

    @Nonnull
    BlockHitResult raycastBlock(Vector3fc from, Vector3fc dir, float distance, Set<Block> ignore);
}
