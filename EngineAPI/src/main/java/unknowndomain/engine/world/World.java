package unknowndomain.engine.world;

import org.joml.Vector3f;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * World instance, should spawn by {@link unknowndomain.engine.game.Game}
 */
public interface World extends RuntimeObject {
    List<Entity> getEntities();

    BlockPrototype.Hit raycast(Vector3f from, Vector3f dir, float distance);

    BlockPrototype.Hit raycast(Vector3f from, Vector3f dir, float distance, Set<Block> ignore);

    @Nonnull
    Block getBlock(@Nonnull BlockPos pos);

    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, Block block);

    interface Config {
        
    }
}
