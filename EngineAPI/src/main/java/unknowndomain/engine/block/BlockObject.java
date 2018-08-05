package unknowndomain.engine.block;

import com.google.common.collect.ImmutableMap;
import org.joml.AABBd;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.RuntimeObject;


public interface BlockObject extends RegistryEntry<BlockObject>, RuntimeObject, Block.PlaceBehavior, Block.ActiveBehavior, Block.DestroyBehavior, Block.TouchBehavior {
    // think about blockstate and tileentity...
    ImmutableMap<Block.Property<?>, Comparable<?>> getProperties();

    <T extends Comparable<T>> T getProperty(Block.Property<T> property);

    <T extends Comparable<T>, V extends T> BlockObject withProperty(Block.Property<T> property, V value);

    <T extends Comparable<T>> BlockObject cycleProperty(Block.Property<T> property);

    AABBd getBoundingBox();
}
