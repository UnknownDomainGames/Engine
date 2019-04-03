package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

class BlockShared extends RegistryEntry.Impl<Block> implements Block {
    private AABBd[] boundingBox;
    private BlockPrototype.PlaceBehavior placeBehavior;
    private BlockPrototype.ActivateBehavior activateBehavior;
    private BlockPrototype.ClickBehavior clickBehavior;
    private BlockPrototype.DestroyBehavior destroyBehavior;

    BlockShared(AABBd[] boundingBox, BlockPrototype.PlaceBehavior placeBehavior, BlockPrototype.ActivateBehavior activateBehavior, BlockPrototype.ClickBehavior clickBehavior, BlockPrototype.DestroyBehavior destroyBehavior) {
        this.boundingBox = boundingBox;
        this.placeBehavior = placeBehavior;
        this.activateBehavior = activateBehavior;
        this.clickBehavior = clickBehavior;
        this.destroyBehavior = destroyBehavior;
    }

    @Override
    public boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
        return placeBehavior.canPlace(world, entity, blockPos, block, cause);
    }

    @Override
    public void onPlaced(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
        placeBehavior.onPlaced(world, entity, blockPos, block, cause);
    }

    @Override
    public boolean onClicked(World world, BlockPos pos, Block block) {
        return clickBehavior.onClicked(world, pos, block);
    }

    @Override
    public boolean onActivated(World world, Entity entity, BlockPos pos, Block block) {
        return activateBehavior.onActivated(world, entity, pos, block);
    }

    @Override
    public boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
        return destroyBehavior.canDestroy(world, entity, blockPos, block, cause);
    }

    @Override
    public void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
        destroyBehavior.onDestroyed(world, entity, blockPos, block, cause);
    }

//    @Nullable
//    @Override
//    @SuppressWarnings("unchecked")
//    public <T> T getBehavior(@Nonnull Class<T> type) {
//        if (type == BlockPrototype.PlaceBehavior.class) return (T) placeBehavior;
//        else if (type == BlockPrototype.ActiveBehavior.class) return (T) activeBehavior;
//        else if (type == BlockPrototype.TouchBehavior.class) return (T) touchBehavior;
//        else if (type == BlockPrototype.DestroyBehavior.class) return (T) destroyBehavior;
//        return null;
//    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return boundingBox;
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, T value) {

    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {

    }

    @Override
    public void onRandomTick(World world, BlockPos pos, Block block) {

    }

    @Override
    public void onChange(World world, BlockPos pos, Block block, BlockChangeCause cause) {

    }
}
