package nullengine.event.world;

import nullengine.event.EventBase;
import nullengine.world.World;

public class WorldTickEvent extends EventBase {
    private World world;

    public WorldTickEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
