package engine.event.block.cause;

import engine.entity.Entity;
import engine.event.cause.Cause;
import engine.player.Player;

public interface BlockChangeCause extends Cause {

    class PlayerCause implements BlockChangeCause {
        private final Player player;

        public PlayerCause(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public String getName() {
            return "PlayerBlockChange";
        }
    }

    class EntityCause implements BlockChangeCause {

        private final Entity entity;

        public EntityCause(Entity entity) {
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }

        @Override
        public String getName() {
            return "EntityBlockChange";
        }
    }

    class WorldGenCause implements BlockChangeCause {

        @Override
        public String getName() {
            return "WorldGenBlockChange";
        }
    }


    class WorldSyncCause implements BlockChangeCause {

        @Override
        public String getName() {
            return "WorldSyncBlockChange";
        }
    }
}
