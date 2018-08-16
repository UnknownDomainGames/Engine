package unknowndomain.engine.mod;

import unknowndomain.engine.util.versioning.ComparableVersion;

import java.util.List;
import java.util.Map;

public interface ModMetadata {
	
	String getName();

	ComparableVersion getVersion();

	String getDescription();
	
	String getUrl();
	
	List<String> getAuthors();
	
	String getLogoFile();
	
	Map<String, Object> getProperties();
}
