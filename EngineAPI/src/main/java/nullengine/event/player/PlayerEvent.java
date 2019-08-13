package nullengine.event.player;

import nullengine.event.Event;
import nullengine.player.Player;

public class PlayerEvent implements Event {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
