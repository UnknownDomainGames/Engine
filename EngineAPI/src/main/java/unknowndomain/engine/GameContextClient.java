package unknowndomain.engine;

import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.registry.RegistryManager;

public class GameContextClient extends GameContext {
    private Player player;

    public GameContextClient(RegistryManager manager, EventBus bus, Player player) {
        super(manager, bus, nextTick);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
