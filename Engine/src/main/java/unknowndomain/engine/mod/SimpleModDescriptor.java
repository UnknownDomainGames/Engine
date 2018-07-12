package unknowndomain.engine.mod;

import java.util.List;

import unknowndomain.engine.api.mod.ModDescriptor;

public class SimpleModDescriptor implements ModDescriptor {
	
	private String name;
	private String description;
	private String url;
	private List<String> authors;
	private String logoFile;
	
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
}
