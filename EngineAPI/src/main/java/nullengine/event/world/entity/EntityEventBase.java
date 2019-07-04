package nullengine.event.world.entity;

import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.event.EventBase;
import nullengine.item.ItemStack;
import nullengine.world.World;

import java.util.Optional;

public abstract class EntityEventBase extends EventBase.Cancellable implements Event {
    private World world;
    private Entity entity;
    private ItemStack itemStack;

    public EntityEventBase(World world, Entity entity, ItemStack itemStack) {
        this.world = world;
        this.entity = entity;
        this.itemStack = itemStack;
    }

    public World getWorld() {
        return world;
    }

    public Entity getEntity() {
        return entity;
    }

    public Optional<ItemStack> getItemStack() {
        return Optional.ofNullable(itemStack);
    }
}
