package com.github.unknownstudio.unknowndomain.engineapi.mod;

public interface ModManager {
	
	ModContainer getMod(String modid);
	
	boolean isModLoaded(String modid);

}
