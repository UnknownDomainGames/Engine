package unknowndomain.engine.game;

import unknowndomain.engine.Prototype;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.world.World;

import java.util.Collection;

import javax.annotation.Nullable;

public interface Game extends RuntimeObject, Prototype<RuntimeObject, Game> {
    
	Collection<World> getWorlds();
	
	@Nullable
	World getWorld(String name);
	
	void tick();
	
	void addWorld(World world);
}
