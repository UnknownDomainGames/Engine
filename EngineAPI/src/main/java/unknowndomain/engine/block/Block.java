package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.GameObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

public interface Block extends RegistryEntry<Block>, GameObject {
    // think about blockstate and tileentity...

    AABBd DEFAULT_BOUNDING_BOX = new AABBd(0, 0, 0, 1, 1, 1);

    @Deprecated
    AABBd[] getBoundingBoxes();

    default AABBd[] getBoundingBoxes(World world, BlockPos pos, Block block) {
        return getBoundingBoxes();
    }
}
