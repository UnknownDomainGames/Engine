package unknowndomain.engine.item;

import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.util.Owner;
import unknowndomain.engine.world.World;

@Owner(World.class)
public interface Item extends RuntimeObject, RegistryEntry<Item>,
        ItemPrototype.UseBlockBehavior, ItemPrototype.HitBlockBehavior, ItemPrototype.UseBehavior {

}
