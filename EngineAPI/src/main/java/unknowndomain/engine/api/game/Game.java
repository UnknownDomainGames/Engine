package unknowndomain.engine.api.game;

import java.util.Collection;

import javax.annotation.Nullable;

import unknowndomain.engine.api.world.World;

public interface Game {
    
	Collection<World> getWorlds();
	
	@Nullable
	World getWorld(String name);
}
