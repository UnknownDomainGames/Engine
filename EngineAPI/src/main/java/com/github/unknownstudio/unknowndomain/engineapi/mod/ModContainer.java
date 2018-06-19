package com.github.unknownstudio.unknowndomain.engineapi.mod;

import com.github.unknownstudio.unknowndomain.engineapi.util.versioning.ComparableVersion;

public class ModContainer {
    private String modid;

    private ComparableVersion version;

    private String name;

    public ModContainer(String modid, String name, String version){
        this.modid = modid;
        this.name = name;
        this.version = new ComparableVersion(version);
    }
}
