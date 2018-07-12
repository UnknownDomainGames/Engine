package unknowndomain.engine.api.mod;

import java.util.List;

public interface ModDescriptor {
	
	String getName();

	String getDescription();
	
	String getUrl();
	
	List<String> getAuthors();
	
	String getLogoFile();
}
