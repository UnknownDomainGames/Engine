package unknowndomain.engine.api.mod;

import java.nio.file.Path;

import org.slf4j.Logger;

import unknowndomain.engine.api.util.versioning.ComparableVersion;

public interface ModContainer {

	String getModId();

	ComparableVersion getVersion();

	Object getMainClassInstance();
	
	Path getSource();
	
	Logger getLogger();
	
	boolean isEnable();
	
	void setEnable(boolean enable);
}
