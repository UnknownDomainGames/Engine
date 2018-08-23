package unknowndomain.engine.mod;

import java.nio.file.Path;

import org.slf4j.Logger;

import unknowndomain.engine.util.versioning.ComparableVersion;

public interface ModContainer {

	String getModId();

	Object getInstance();
	
	Path getSource();
	
	Logger getLogger();
	
	ModMetadata getMetadata();

	ModState getState();

	Path getDataDir();
}
