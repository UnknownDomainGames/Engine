package unknowndomain.engine.client.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.world.World;

public class GameClient implements Game {

	private Map<String, World> worlds = new HashMap<>();
	
	@Override
	public Collection<World> getWorlds() {
		return worlds.values();
	}

	@Override
	public World getWorld(String name) {
		return worlds.get(name);
	}
	
	
}
