package unknowndomain.engine.api.mod;

public interface ModManager {
	
	ModContainer getMod(String modId);
	
	boolean isModLoaded(String modId);

}
