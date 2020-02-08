package engine.event.entity;

import engine.entity.Entity;
import engine.event.Cancellable;

public abstract class EntitySpawnEvent extends EntityEvent {

    EntitySpawnEvent(Entity entity) {
        super(entity);
    }

    public static final class Pre extends EntitySpawnEvent implements Cancellable {

        private boolean cancelled = false;

        public Pre(Entity entity) {
            super(entity);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static final class Post extends EntitySpawnEvent {

        public Post(Entity entity) {
            super(entity);
        }
    }
}
