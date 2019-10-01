package nullengine.event.world;

import nullengine.world.World;

public class WorldUnloadEvent extends WorldEvent {
    public WorldUnloadEvent(World world) {
        super(world);
    }
}
