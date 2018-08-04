package unknowndomain.engine.unclassified;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.joml.AABBd;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class BlockObjectShared extends RegistryEntry.Impl<BlockObject> implements BlockObject {
    ImmutableTable<Block.Property<?>, Comparable<?>, BlockObjectShared> propertiesTable;
    private AABBd boundingBox;
    private Block.PlaceBehavior placeBehavior;
    private Block.ActiveBehavior activeBehavior;
    private Block.TouchBehavior touchBehavior;
    private Block.DestroyBehavior destroyBehavior;
    private ImmutableMap<Block.Property<?>, Comparable<?>> properties;

    BlockObjectShared(AABBd boundingBox, Block.PlaceBehavior placeBehavior, Block.ActiveBehavior activeBehavior, Block.TouchBehavior touchBehavior, Block.DestroyBehavior destroyBehavior, ImmutableMap<Block.Property<?>, Comparable<?>> properties) {
        this.boundingBox = boundingBox;
        this.placeBehavior = placeBehavior;
        this.activeBehavior = activeBehavior;
        this.touchBehavior = touchBehavior;
        this.destroyBehavior = destroyBehavior;
        this.properties = properties;
    }

    public boolean onPrePlace(BlockObject block) {
        return placeBehavior.onPrePlace(block);
    }

    public void onPlaced(BlockObject block) {
        placeBehavior.onPlaced(block);
    }

    @Override
    public boolean onActivate(BlockObject block) {
        return activeBehavior.onActivate(block);
    }

    @Override
    public void onActivated(BlockObject block) {
        activeBehavior.onActivated(block);
    }

    @Override
    public boolean onTouch(BlockObject block) {
        return touchBehavior.onTouch(block);
    }

    @Override
    public void onTouched(BlockObject block) {
        touchBehavior.onTouched(block);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBehavior(@Nonnull Class<T> type) {
        if (type == Block.PlaceBehavior.class) return (T) placeBehavior;
        else if (type == Block.ActiveBehavior.class) return (T) activeBehavior;
        else if (type == Block.TouchBehavior.class) return (T) touchBehavior;
        else if (type == Block.DestroyBehavior.class) return (T) destroyBehavior;
        return null;
    }

    @Override
    public ImmutableMap<Block.Property<?>, Comparable<?>> getProperties() {
        return properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getProperty(Block.Property<T> property) {
        return (T) properties.get(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> BlockObject withProperty(Block.Property<T> property, V value) {
        return propertiesTable.get(property, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> BlockObject cycleProperty(Block.Property<T> property) {
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
    public AABBd getBoundingBox() {
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
