package nullengine.event.world;

import nullengine.event.Event;
import nullengine.world.World;

public class WorldEvent implements Event {

    private final World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
