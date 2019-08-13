package nullengine.event.block.cause;

import nullengine.entity.Entity;
import nullengine.event.cause.Cause;

public interface BlockChangeCause extends Cause {
    class EntityCause implements BlockChangeCause{

        private Entity entity;

        public EntityCause(Entity entity){
            this.entity = entity;
        }

        @Override
        public String getCauseName() {
            return "entity_block_change";
        }
    }

    class CommandCause implements BlockChangeCause{

        @Override
        public String getCauseName() {
            return "command_block_change";
        }
    }

    class WorldGenCause implements BlockChangeCause{

        @Override
        public String getCauseName() {
            return "worldgen_block_change";
        }
    }
}
