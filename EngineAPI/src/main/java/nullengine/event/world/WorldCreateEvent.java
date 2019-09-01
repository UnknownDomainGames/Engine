package nullengine.event.world;

import nullengine.world.World;

public final class WorldCreateEvent extends WorldEvent {
    public WorldCreateEvent(World world) {
        super(world);
    }
}
