package unknowndomain.engine.block;

import com.google.common.collect.ImmutableMap;
import org.joml.AABBd;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Impl;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockAir extends Impl<Block> implements Block {

    public BlockAir() {
        localName("air");
    }

    @Override
    public ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> getProperties() {
        return null;
    }

    @Override
    public <T extends Comparable<T>> T getProperty(BlockPrototype.Property<T> property) {
        return null;
    }

    @Override
    public <T extends Comparable<T>, V extends T> Block withProperty(BlockPrototype.Property<T> property, V value) {
        return this;
    }

    @Override
    public <T extends Comparable<T>> Block cycleProperty(BlockPrototype.Property<T> property) {
        return this;
    }

    @Override
    public AABBd[] getBoundingBoxes() {
        return new AABBd[0];
    }

    @Override
    public boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block) {
        return false;
    }

    @Override
    public void onPlaced(World world, Entity entity, BlockPos blockPos, Block block) {
    }

    @Override
    public boolean shouldActivated(World world, Entity entity, BlockPos blockPos, Block block) {
        return false;
    }

    @Override
    public void onActivated(World world, Entity entity, BlockPos pos, Block block) {

    }

    @Override
    public boolean onTouch(Block block) {
        return false;
    }

    @Override
    public void onTouched(Block block) {

    }

    @Override
    public boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block) {
        return false;
    }

    @Override
    public void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block) {

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
