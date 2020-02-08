package engine.event.world;

import engine.world.World;

public class WorldLoadEvent extends WorldEvent {
    public WorldLoadEvent(World world) {
        super(world);
    }
}
