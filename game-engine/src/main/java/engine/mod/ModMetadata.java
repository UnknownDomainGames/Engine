package engine.mod;

import com.google.gson.JsonElement;
import engine.util.versioning.Version;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static engine.mod.InstallationType.CLIENT_REQUIRED;

public class ModMetadata {

    public static final Version DEFAULT_VERSION = new Version("1.0.0");

    private final String id;
    private final Version version;
    private final String mainClass;
    private final String name;
    private final InstallationType installationType;
    private final String description;
    private final String license;
    private final String url;
    private final String logo;
    private final List<String> authors;
    private final List<String> credits;
    private final List<String> permissions;
    private final List<Dependency> dependencies;
    private final Map<String, JsonElement> elements;

    protected ModMetadata(String id, Version version, String mainClass, String name, InstallationType installationType, String description, String license, String url, String logo, List<String> authors, List<String> credits, List<String> permissions, List<Dependency> dependencies, Map<String, JsonElement> elements) {
        this.id = id;
        this.version = version;
        this.mainClass = mainClass;
        this.name = name;
        this.installationType = installationType;
        this.description = description;
        this.license = license;
        this.url = url;
        this.logo = logo;
        this.authors = authors;
        this.credits = credits;
        this.permissions = permissions;
        this.dependencies = dependencies;
        this.elements = elements;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public Version getVersion() {
        return version;
    }

    @Nonnull
    public String getMainClass() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public InstallationType getInstallationType() {
        return installationType;
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

    public String getLogoFile() {
        return logo;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getCredits() {
        return credits;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public Map<String, JsonElement> getCustomElements() {
        return elements;
    }

    public Optional<JsonElement> getCustomElement(String key) {
        return Optional.ofNullable(elements.get(key));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = "";
        private Version version = DEFAULT_VERSION;
        private String mainClass = "";
        private String name = "";
        private InstallationType installationType = CLIENT_REQUIRED;
        private String description = "";
        private String license = "";
        private String url = "";
        private String logo = "";
        private List<String> authors = List.of();
        private List<String> credits = List.of();
        private List<String> permissions = List.of();
        private List<Dependency> dependencies = List.of();
        private Map<String, JsonElement> elements = Map.of();

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

        public Builder installationType(InstallationType installationType) {
            this.installationType = installationType;
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

        public Builder authors(String... authors) {
            this.authors = List.of(authors);
            return this;
        }

        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder credits(String... credits) {
            this.credits = List.of(credits);
            return this;
        }

        public Builder credits(List<String> credits) {
            this.credits = credits;
            return this;
        }

        public Builder permissions(String... permissions) {
            this.permissions = List.of(permissions);
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder dependencies(Dependency... dependencies) {
            this.dependencies = List.of(dependencies);
            return this;
        }

        public Builder dependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public Builder elements(Map<String, JsonElement> elements) {
            this.elements = elements;
            return this;
        }

        public ModMetadata build() {
            return new ModMetadata(id, version, mainClass, name, installationType, description, license, url, logo, authors, credits, permissions, dependencies, elements);
        }
    }
}
