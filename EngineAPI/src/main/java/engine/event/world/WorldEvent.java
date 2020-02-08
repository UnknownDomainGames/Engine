package engine.event.world;

import engine.event.Event;
import engine.world.World;

public class WorldEvent implements Event {

    private final World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
