package unknowndomain.engine.client.world;

import unknowndomain.engine.api.world.World;
import unknowndomain.engine.api.world.WorldProvider;

public class BasicWorldProvider extends WorldProvider{

	@Override
	public String getName() {
		return "BasicWorldProvider";
	}

	@Override
	public World createWorld(String worldName) {
		
		return null;
	}

}
