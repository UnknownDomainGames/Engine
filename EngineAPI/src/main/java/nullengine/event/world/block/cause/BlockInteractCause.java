package nullengine.event.world.block.cause;

import java.util.Optional;

import nullengine.entity.Entity;
import nullengine.event.cause.Cause;
import nullengine.item.ItemStack;

public interface BlockInteractCause extends Cause {
    class EntityTrigger implements BlockInteractCause {
        public static final String HIT = "entity.hit", USE = "entity.use";

        public static EntityTrigger hit(Entity entity, ItemStack item) {
            return new EntityTrigger(entity, item, HIT);
        }

        public static EntityTrigger use(Entity entity, ItemStack item) {
            return new EntityTrigger(entity, item, USE);
        }

        private Entity entity;
        private Optional<ItemStack> item;
        private String causeName;

        public EntityTrigger(Entity entity, ItemStack item, String causeName) {
            this.entity = entity;
            this.item = Optional.ofNullable(item);
            this.causeName = causeName;
        }

        @Override
        public String getCauseName() {
            return causeName;
        }

        public Entity getEntity() {
            return this.entity;
        }

        public Optional<ItemStack> getHeldItem() {
            return this.item;
        }
    }

    interface CommandTrigger extends BlockInteractCause {
    }
}
