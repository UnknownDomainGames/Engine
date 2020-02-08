package engine.event.world;

import engine.world.World;

public class WorldUnloadEvent extends WorldEvent {
    public WorldUnloadEvent(World world) {
        super(world);
    }
}
