package unknowndomain.engine.api.mod;

public interface ModManager {
	
	ModContainer getMod(String modid);
	
	boolean isModLoaded(String modid);

}
