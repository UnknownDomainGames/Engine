package unknowndomain.engine.api.world;

public interface WorldProvider {
	
	String getName();
	
	World createWorld(String worldName);

}
