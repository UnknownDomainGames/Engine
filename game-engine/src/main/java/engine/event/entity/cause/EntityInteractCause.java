package engine.event.entity.cause;

import engine.event.cause.Cause;
import engine.player.Player;

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
