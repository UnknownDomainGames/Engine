package nullengine.event.block.cause;

import nullengine.event.cause.Cause;
import nullengine.player.Player;

public interface BlockInteractCause extends Cause {

    class PlayerCause implements BlockInteractCause {

        private final Player player;

        public PlayerCause(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public String getName() {
            return "PlayerBlockInteract";
        }
    }
}
