package nullengine.event.world.entity;

import nullengine.entity.Entity;
import nullengine.item.ItemStack;
import nullengine.world.World;

public class EntityUseEvent extends EntityEventBase {
    public EntityUseEvent(World world, Entity entity, ItemStack itemStack) {
        super(world, entity, itemStack);
    }
}
