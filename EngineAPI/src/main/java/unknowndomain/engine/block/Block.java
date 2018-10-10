package unknowndomain.engine.block;

import com.google.common.collect.ImmutableMap;
import org.joml.AABBd;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Owner;
import unknowndomain.engine.world.World;


@Owner({World.class, Game.class})
public interface Block extends RegistryEntry<Block>, RuntimeObject, BlockPrototype.PlaceBehavior, BlockPrototype.ActiveBehavior, BlockPrototype.DestroyBehavior, BlockPrototype.TouchBehavior {
    // think about blockstate and tileentity...

    AABBd DEFAULT_BOUNDING_BOX = new AABBd(0, 0, 0, 1, 1, 1);

    ImmutableMap<BlockPrototype.Property<?>, Comparable<?>> getProperties();

    <T extends Comparable<T>> T getProperty(BlockPrototype.Property<T> property);

    <T extends Comparable<T>, V extends T> Block withProperty(BlockPrototype.Property<T> property, V value);

    <T extends Comparable<T>> Block cycleProperty(BlockPrototype.Property<T> property);

    AABBd[] getBoundingBoxes();
}
