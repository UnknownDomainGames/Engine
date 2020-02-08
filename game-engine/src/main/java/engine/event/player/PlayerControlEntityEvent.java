package engine.event.player;

import engine.entity.Entity;
import engine.event.Cancellable;
import engine.player.Player;

public abstract class PlayerControlEntityEvent extends PlayerEvent {

    private final Entity oldEntity;

    Entity newEntity;

    private PlayerControlEntityEvent(Player player, Entity oldEntity, Entity newEntity) {
        super(player);
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    public final Entity getOldEntity() {
        return oldEntity;
    }

    public final Entity getNewEntity() {
        return newEntity;
    }

    public static final class Pre extends PlayerControlEntityEvent implements Cancellable {

        private boolean cancelled = false;

        public Pre(Player player, Entity oldEntity, Entity newEntity) {
            super(player, oldEntity, newEntity);
        }

        public void setNewEntity(Entity entity) {
            newEntity = entity;
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

    public static final class Post extends PlayerControlEntityEvent {
        public Post(Player player, Entity oldEntity, Entity newEntity) {
            super(player, oldEntity, newEntity);
        }
    }
}
