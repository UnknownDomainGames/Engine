package nullengine.event.block.cause;

import nullengine.entity.Entity;
import nullengine.event.cause.Cause;
import nullengine.player.Player;

public interface BlockChangeCause extends Cause {

    interface PlayerCause extends BlockChangeCause {
        Player getPlayer();
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
        public String getCauseName() {
            return "entity_block_change";
        }
    }

    class PlayerEntityCause extends EntityCause implements PlayerCause {

        private final Player player;

        public PlayerEntityCause(Player player) {
            super(player.getControlledEntity());
            this.player = player;
        }

        @Override
        public Player getPlayer() {
            return player;
        }

        @Override
        public String getCauseName() {
            return "player_entity_block_change";
        }
    }

    class CommandCause implements BlockChangeCause {

        @Override
        public String getCauseName() {
            return "command_block_change";
        }
    }

    class WorldGenCause implements BlockChangeCause {

        @Override
        public String getCauseName() {
            return "worldgen_block_change";
        }
    }
}
