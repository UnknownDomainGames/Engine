package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockAir extends RegistryEntry.Impl<Block> implements Block {

    public BlockAir() {
        localName("air");
    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return new AABBd[0];
    }

    @Nullable
    @Override
    public <T extends Component> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }

    @Override
    public void onPlaced(World world, Entity entity, BlockPos blockPos, Block block) {

    }

    @Override
    public void onActivated(World world, Entity entity, BlockPos pos, Block block) {

    }

    @Override
    public void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block) {

    }

    @Override
    public void onClicked(World world, BlockPos pos, Block block) {

    }
}
