package engine.event.world;

import engine.world.World;

public final class WorldCreateEvent extends WorldEvent {
    public WorldCreateEvent(World world) {
        super(world);
    }
}
