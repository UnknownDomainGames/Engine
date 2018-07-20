package unknowndomain.engine.mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unknowndomain.engine.api.mod.ModMetadata;

public class FileModMetadata implements ModMetadata {
	
	private String name;
	private String description;
	private String url;
	private List<String> authors;
	private String logoFile;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public List<String> getAuthors() {
		return authors;
	}

	@Override
	public String getLogoFile() {
		return logoFile;
	}

	@Override
	public Map<String, Object> getMetadata() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("description", description);
		map.put("url", url);
		map.put("authors", authors);
		map.put("logoFile", logoFile);
		return map;
	}

}
