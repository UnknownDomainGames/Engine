package unknowndomain.engine.unclassified;

import com.google.common.collect.ImmutableMap;
import org.joml.AABBd;
import unknowndomain.engine.Entity;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class BlockRuntime extends RegistryEntry.Impl<Block> implements Block {
    private Map<String, Object> map;
    private Block shared;

    BlockRuntime(Map<String, Object> map, Block shared) {
        this.map = map;
        this.shared = shared;
    }

    @Override
    public ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> getProperties() {
        return shared.getProperties();
    }

    @Override
    public <T extends Comparable<T>> T getProperty(BlockPrototype.Property<T> property) {
        return shared.getProperty(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> Block withProperty(BlockPrototype.Property<T> property, V value) {
        shared = shared.withProperty(property, value);
        return this;
    }

    @Override
    public <T extends Comparable<T>> Block cycleProperty(BlockPrototype.Property<T> property) {
        shared = shared.cycleProperty(property);
        return this;
    }

    @Override
    public AABBd getBoundingBox() {
        return shared.getBoundingBox();
    }

    @Override
    public boolean canPlace(World world, Entity entity, Block block) {
        return shared.canPlace(world, entity, block);
    }

    @Override
    public void onPlaced(World world, Entity entity, Block block) {
        shared.onPlaced(world, entity, block);
    }

    @Override
    public boolean shouldActivated(World world, Entity entity, BlockPos blockPos, Block block) {
        return shared.shouldActivated(world, entity, blockPos, block);
    }

    @Override
    public void onActivated(World world, Entity entity, BlockPos pos, Block block) {
        shared.onActivated(world, entity, pos, block);
    }

    @Override
    public boolean onTouch(Block block) {
        return shared.onTouch(block);
    }

    @Override
    public void onTouched(Block block) {
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
