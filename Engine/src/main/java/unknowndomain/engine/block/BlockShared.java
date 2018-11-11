package unknowndomain.engine.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.joml.AABBd;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Impl;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class BlockShared extends Impl<Block> implements Block {
    ImmutableTable<BlockPrototype.Property<?>, Comparable<?>, BlockShared> propertiesTable;
    private AABBd[] boundingBox;
    private BlockPrototype.PlaceBehavior placeBehavior;
    private BlockPrototype.ActiveBehavior activeBehavior;
    private BlockPrototype.TouchBehavior touchBehavior;
    private BlockPrototype.DestroyBehavior destroyBehavior;
    private ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> properties;

    BlockShared(AABBd[] boundingBox, BlockPrototype.PlaceBehavior placeBehavior, BlockPrototype.ActiveBehavior activeBehavior, BlockPrototype.TouchBehavior touchBehavior, BlockPrototype.DestroyBehavior destroyBehavior, ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> properties) {
        this.boundingBox = boundingBox;
        this.placeBehavior = placeBehavior;
        this.activeBehavior = activeBehavior;
        this.touchBehavior = touchBehavior;
        this.destroyBehavior = destroyBehavior;
        this.properties = properties;
    }

    @Override
    public boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block) {
        return placeBehavior.canPlace(world,entity,blockPos,block);
    }

    @Override
    public void onPlaced(World world, Entity entity, BlockPos blockPos, Block block) {
        placeBehavior.onPlaced(world,entity,blockPos,block);
    }

    @Override
    public boolean shouldActivated(World world, Entity entity, BlockPos blockPos, Block block) {
        return activeBehavior.shouldActivated(world, entity, blockPos, block);
    }

    @Override
    public void onActivated(World world, Entity entity, BlockPos pos, Block block) {
        activeBehavior.onActivated(world, entity, pos, block);
    }

    @Override
    public boolean onTouch(Block block) {
        return touchBehavior.onTouch(block);
    }

    @Override
    public void onTouched(Block block) {
        touchBehavior.onTouched(block);
    }

    @Override
    public boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block) {
        return destroyBehavior.canDestroy(world, entity, blockPos, block);
    }

    @Override
    public void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block) {
        destroyBehavior.onDestroyed(world, entity, blockPos, block);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBehavior(@Nonnull Class<T> type) {
        if (type == BlockPrototype.PlaceBehavior.class) return (T) placeBehavior;
        else if (type == BlockPrototype.ActiveBehavior.class) return (T) activeBehavior;
        else if (type == BlockPrototype.TouchBehavior.class) return (T) touchBehavior;
        else if (type == BlockPrototype.DestroyBehavior.class) return (T) destroyBehavior;
        return null;
    }

    @Override
    public ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> getProperties() {
        return properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getProperty(BlockPrototype.Property<T> property) {
        return (T) properties.get(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> Block withProperty(BlockPrototype.Property<T> property, V value) {
        return propertiesTable.get(property, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> Block cycleProperty(BlockPrototype.Property<T> property) {
        ImmutableList<T> values = property.getValues();
        T current = (T) properties.get(property);
        for (int i = 0; i < values.size(); i++) {
            T next = values.get(i);
            if (next.compareTo(current) == 0) {
                if (i + 1 == values.size()) {
                    return withProperty(property, values.get(0));
                }
                return withProperty(property, values.get(i + 1));
            }
        }
        throw new Error("Hummmm");
    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return boundingBox;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }
}
