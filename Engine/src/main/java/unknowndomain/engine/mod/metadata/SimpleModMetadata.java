package unknowndomain.engine.mod.metadata;

import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleModMetadata implements ModMetadata {

    private String name;
    private ComparableVersion version;
    private String description;
    private String url;
    private List<String> authors;
    private String logoFile;
    private final Map<String, Object> properties = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ComparableVersion getVersion() {
        return version;
    }

    public void setVersion(ComparableVersion version) {
        this.version = version;
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
    public Map<String, Object> getProperties() {
        return properties;
    }
}
