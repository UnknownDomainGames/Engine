package unknowndomain.engine._mod;

import unknowndomain.engine.util.versioning.ComparableVersion;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModMetadata extends ModIdentifier {

    public static final ComparableVersion UNKNOWN_VERSION = new ComparableVersion("unknown");

    private final Path source;
    private final String mainClass;
    private final String name;
    private final String description;
    private final String url;
    private final List<String> authors;
    private final String logoFile;
    private final List<ModDependencyEntry> dependencies;
    private final Map<String, String> properties;

    protected ModMetadata(String id, ComparableVersion version, Path source, String mainClass, String name, String description, String url, List<String> authors, String logoFile, List<ModDependencyEntry> dependencies, Map<String, String> properties) {
        super(id, version);
        this.source = source;
        this.mainClass = mainClass;
        this.name = name;
        this.description = description;
        this.url = url;
        this.authors = authors;
        this.logoFile = logoFile;
        this.dependencies = dependencies;
        this.properties = properties;
    }

    public Path getSource() {
        return source;
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

    public String getUrl() {
        return url;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getLogoFile() {
        return logoFile;
    }

    public List<ModDependencyEntry> getDependencies() {
        return dependencies;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static class Builder {
        protected String id = "";
        protected ComparableVersion version = UNKNOWN_VERSION;
        protected Path source;
        protected String mainClass = "";
        protected String name = "";
        protected String description = "";
        protected String url = "";
        protected List<String> authors = Collections.emptyList();
        protected String logo = "";
        protected List<ModDependencyEntry> dependency = Collections.emptyList();
        protected Map<String, String> properties = Collections.emptyMap();

        public static Builder create() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder version(ComparableVersion version) {
            this.version = version;
            return this;
        }

        public Builder version(String version) {
            this.version = new ComparableVersion(version);
            return this;
        }

        public Builder source(Path source) {
            this.source = source;
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

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public Builder dependencies(List<ModDependencyEntry> dependency) {
            this.dependency = dependency;
            return this;
        }

        public ModMetadata build() {
            return new ModMetadata(id, version, source, mainClass, name, description, url, authors, logo, dependency, properties);
        }
    }
}
