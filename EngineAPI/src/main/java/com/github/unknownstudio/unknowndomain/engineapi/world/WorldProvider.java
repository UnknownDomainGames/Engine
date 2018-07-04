package com.github.unknownstudio.unknowndomain.engineapi.world;

public interface WorldProvider {
	
	String getName();
	
	World createWorld(String worldName);

}
