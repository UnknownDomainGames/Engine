package nullengine.event.item.cause;

import nullengine.event.cause.Cause;
import nullengine.player.Player;

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
