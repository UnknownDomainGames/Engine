package unknowndomain.engine.mod;

public interface ModLoader {
	
	ModContainer loadMod(String modId);
	
	ModContainer getModContainer(String modId);
	
	boolean hasMod(String modId);

}
