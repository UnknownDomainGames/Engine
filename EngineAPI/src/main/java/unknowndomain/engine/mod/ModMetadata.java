package unknowndomain.engine.mod;

import java.util.List;
import java.util.Map;

public interface ModMetadata {
	
	String getName();

	String getDescription();
	
	String getUrl();
	
	List<String> getAuthors();
	
	String getLogoFile();
	
	Map<String, Object> getMetadata();
}
