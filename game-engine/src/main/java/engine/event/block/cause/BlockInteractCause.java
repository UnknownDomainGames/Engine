package engine.event.block.cause;

import engine.event.cause.Cause;
import engine.player.Player;

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
