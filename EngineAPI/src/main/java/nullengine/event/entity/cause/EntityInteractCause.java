package nullengine.event.entity.cause;

import nullengine.event.cause.Cause;
import nullengine.player.Player;

public interface EntityInteractCause extends Cause {

    class PlayerCause implements EntityInteractCause {

        private final Player player;

        public PlayerCause(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public String getName() {
            return "PlayerEntityInteract";
        }
    }
}
