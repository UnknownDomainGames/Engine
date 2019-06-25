package nullengine.block;

import nullengine.component.Component;
import nullengine.component.GameObject;
import nullengine.math.BlockPos;
import nullengine.registry.RegistryEntry;
import nullengine.world.World;
import org.joml.AABBd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Block extends RegistryEntry<Block>, GameObject {
    // think about blockstate and tileentity...

    AABBd DEFAULT_BOUNDING_BOX = new AABBd(0, 0, 0, 1, 1, 1);

    @Deprecated
    AABBd[] getBoundingBoxes();

    @Deprecated
    default AABBd[] getBoundingBoxes(World world, BlockPos pos, Block block) {
        return getBoundingBoxes();
    }

    <T extends Component> Block addComponent(@Nonnull Class<T> type, @Nullable T value);
}
