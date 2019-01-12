package unknowndomain.engine.item;

import unknowndomain.engine.component.RuntimeObject;
import unknowndomain.engine.registry.RegistryEntry;

public interface Item extends RuntimeObject, RegistryEntry<Item>,
        ItemPrototype.UseBlockBehavior, ItemPrototype.HitBlockBehavior, ItemPrototype.UseBehavior {

}
