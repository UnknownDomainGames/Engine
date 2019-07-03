package nullengine.mod.misc;

import com.google.gson.JsonElement;
import nullengine.mod.ModDependencyItem;
import nullengine.mod.ModMetadata;
import nullengine.util.versioning.Version;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleModMetadata implements ModMetadata {

    public static final Version DEFAULT_VERSION = new Version("1.0.0");

    private final String id;
    private final Version version;
    private final String mainClass;
    private final String name;
    private final String description;
    private final String license;
    private final String url;
    private final String logo;
    private final List<String> authors;
    private final List<String> permissions;
    private final List<ModDependencyItem> dependencies;
    private final Map<String, JsonElement> properties;

    protected SimpleModMetadata(String id, Version version, String mainClass, String name, String description, String license, String url, String logo, List<String> authors, List<String> permissions, List<ModDependencyItem> dependencies, Map<String, JsonElement> properties) {
        this.id = id;
        this.version = version;
        this.mainClass = mainClass;
        this.name = name;
        this.description = description;
        this.license = license;
        this.url = url;
        this.logo = logo;
        this.authors = authors;
        this.permissions = permissions;
        this.dependencies = dependencies;
        this.properties = properties;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public Version getVersion() {
        return version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public List<String> getPermissions() {
        return null;
    }

    public String getLogoFile() {
        return logo;
    }

    public List<ModDependencyItem> getDependencies() {
        return dependencies;
    }

    public Map<String, JsonElement> getProperties() {
        return properties;
    }

    public Optional<JsonElement> getProperty(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = "";
        private Version version = DEFAULT_VERSION;
        private String mainClass = "";
        private String name = "";
        private String description = "";
        private String license = "";
        private String url = "";
        private String logo = "";
        private List<String> authors = Collections.emptyList();
        private List<String> permissions = Collections.emptyList();
        private List<ModDependencyItem> dependencies = Collections.emptyList();
        private Map<String, JsonElement> properties = Collections.emptyMap();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Builder version(String version) {
            this.version = new Version(version);
            return this;
        }

        public Builder mainClass(String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder license(String license) {
            this.license = license;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder properties(Map<String, JsonElement> properties) {
            this.properties = properties;
            return this;
        }

        public Builder dependencies(List<ModDependencyItem> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public SimpleModMetadata build() {
            return new SimpleModMetadata(id, version, mainClass, name, description, license, url, logo, authors, permissions, dependencies, properties);
        }
    }
}
