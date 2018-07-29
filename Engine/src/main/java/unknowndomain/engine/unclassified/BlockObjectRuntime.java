package unknowndomain.engine.unclassified;

import com.google.common.collect.ImmutableMap;
import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.unclassified.Block;
import unknowndomain.engine.api.unclassified.BlockObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

class BlockObjectRuntime extends RegistryEntry.Impl<BlockObject> implements BlockObject {
    private Map<String, Object> map;
    private BlockObject shared;

    BlockObjectRuntime(Map<String, Object> map, BlockObject shared) {
        this.map = map;
        this.shared = shared;
    }

    @Override
    public ImmutableMap<Block.Property<?>, Comparable<?>> getProperties() {
        return shared.getProperties();
    }

    @Override
    public <T extends Comparable<T>> T getProperty(Block.Property<T> property) {
        return shared.getProperty(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> BlockObject withProperty(Block.Property<T> property, V value) {
        shared = shared.withProperty(property, value);
        return this;
    }

    @Override
    public <T extends Comparable<T>> BlockObject cycleProperty(Block.Property<T> property) {
        shared = shared.cycleProperty(property);
        return this;
    }

    @Override
    public boolean onPrePlace(BlockObject block) {
        return shared.onPrePlace(block);
    }

    @Override
    public void onPlaced(BlockObject block) {
        shared.onPlaced(block);
    }

    @Override
    public boolean onActivate(BlockObject block) {
        return shared.onActivate(block);
    }

    @Override
    public void onActivated(BlockObject block) {
        shared.onActivated(block);
    }

    @Override
    public boolean onTouch(BlockObject block) {
        return shared.onTouch(block);
    }

    @Override
    public void onTouched(BlockObject block) {
        shared.onTouched(block);
    }

    @Override
    @Nullable
    public <T> T getBehavior(@Nonnull Class<T> type) {
        return shared.getBehavior(type);
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return (T) map.get(name);
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return (T) map.get(type.getName());
    }
}
