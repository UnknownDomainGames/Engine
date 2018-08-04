package unknowndomain.engine.mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleModMetadata implements ModMetadata {
	
	private String name;
	private String description;
	private String url;
	private List<String> authors;
	private String logoFile;
	private final Map<String, Object> metadata = new HashMap<>();
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	@Override
	public String getLogoFile() {
		return logoFile;
	}
	
	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}
}
