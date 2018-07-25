package unknowndomain.engine.world;

import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.api.world.WorldProvider;

public class SimpleWorldProvider extends WorldProvider {

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public World createWorld(String worldName) {
        SimpleWorld world = new SimpleWorld(worldName);
        return world;
    }


}
