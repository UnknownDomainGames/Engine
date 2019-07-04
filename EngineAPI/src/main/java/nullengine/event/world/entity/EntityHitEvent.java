package nullengine.event.world.entity;

import nullengine.entity.Entity;
import nullengine.item.ItemStack;
import nullengine.world.World;

public class EntityHitEvent extends EntityEventBase {
    public EntityHitEvent(World world, Entity entity, ItemStack itemStack) {
        super(world, entity, itemStack);
    }
}
