package unknowndomain.engine.item;

import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.registry.RegistryEntry;

public interface Item extends RuntimeObject, RegistryEntry<Item>,
        ItemPrototype.UseBlockBehavior, ItemPrototype.HitBlockBehavior, ItemPrototype.UseBehavior {

}
