package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block) {
        return placeBehavior.canPlace(world, entity, blockPos, block);
    }

    @Override
    public void onPlaced(World world, Entity entity, BlockPos blockPos, Block block) {
        placeBehavior.onPlaced(world, entity, blockPos, block);
    }

    @Override
    public boolean canActivate(World world, Entity entity, BlockPos blockPos, Block block) {
        return activateBehavior.canActivate(world, entity, blockPos, block);
    }

    @Override
    public boolean canClick(World world, BlockPos pos, Block block) {
        return clickBehavior.canClick(world, pos, block);
    }

    @Override
    public void onClicked(World world, BlockPos pos, Block block) {
        clickBehavior.onClicked(world, pos, block);
    }

    @Override
    public void onActivated(World world, Entity entity, BlockPos pos, Block block) {
        activateBehavior.onActivated(world, entity, pos, block);
    }

    @Override
    public boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block) {
        return destroyBehavior.canDestroy(world, entity, blockPos, block);
    }

    @Override
    public void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block) {
        destroyBehavior.onDestroyed(world, entity, blockPos, block);
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

    @Nullable
    @Override
    public <T extends Component> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }
}
