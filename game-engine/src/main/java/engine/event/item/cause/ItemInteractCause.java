package engine.event.item.cause;

import engine.event.cause.Cause;
import engine.player.Player;

public interface ItemInteractCause extends Cause {

    class PlayerCause implements ItemInteractCause {

        private final Player player;

        public PlayerCause(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public String getName() {
            return "PlayerItemInteract";
        }
    }
}
