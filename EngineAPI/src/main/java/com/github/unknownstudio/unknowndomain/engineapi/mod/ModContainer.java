package com.github.unknownstudio.unknowndomain.engineapi.mod;

import com.github.unknownstudio.unknowndomain.engineapi.util.versioning.ComparableVersion;

public class ModContainer {
	
    private final String modid;
    private final ComparableVersion version;
    
    private String name;
    
    private Object instance;

    public ModContainer(String modid, String version){
        this.modid = modid;
        this.version = new ComparableVersion(version);
    }

	public String getModid() {
		return modid;
	}

	public ComparableVersion getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getInstance() {
		return instance;
	}
}
