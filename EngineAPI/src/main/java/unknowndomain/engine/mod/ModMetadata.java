package unknowndomain.engine.mod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import unknowndomain.engine.util.JsonUtils;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class ModMetadata extends ModIdentifier {

    public static final ComparableVersion UNKNOWN_VERSION = new ComparableVersion("unknown");

    private final String name;
    private final String description;
    private final String url;
    private final List<String> authors;
    private final String logoFile; // base 64 logo?
    private final List<ModDependencyEntry> dependencies;
    private final Map<String, Object> properties;

    protected ModMetadata(String group, String id, ComparableVersion version, String name, String description, String url, List<String> authors, String logoFile, List<ModDependencyEntry> dependencies, Map<String, Object> properties) {
        super(group, id, version);
        this.name = name;
        this.description = description;
        this.url = url;
        this.authors = authors;
        this.logoFile = logoFile;
        this.dependencies = dependencies;
        this.properties = properties;
    }

    public static ModMetadata fromJson(JsonObject jo) {
        String modid = "", name = "", url = "", description = "", logoFile = "";
        List<String> authors = Collections.emptyList();
        List<ModDependencyEntry> dependencies = Collections.emptyList();
        Map<String, Object> properties = Collections.emptyMap();
        ComparableVersion version = UNKNOWN_VERSION;
        // TODO make default and validate id & version in metadata

        if (jo.has("id")) {
            modid = jo.get("id").getAsString();
        }
        if (jo.has("name")) {
            name = jo.get("name").getAsString();
        }
        if (jo.has("version")) {
            version = new ComparableVersion(jo.get("version").getAsString());
        }
        if (jo.has("description")) {
            description = jo.get("description").getAsString();
        }
        if (jo.has("url")) {
            url = jo.get("url").getAsString();
        }
        if (jo.has("logoFile")) {
            logoFile = jo.get("logoFile").getAsString();
        }
        if (jo.has("authors")) {
            authors = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("authors")) {
                if (je.isJsonPrimitive()) {
                    authors.add(je.getAsString());
                }
            }
            authors = ImmutableList.copyOf(authors);
        }
        if (jo.has("dependencies")) {
            dependencies = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("dependencies")) {
                dependencies.add(ModDependencyEntry.parse(je.getAsString()));
            }
        }
        if (jo.has("properties")) {
            properties = new HashMap<>();
            JsonObject jProperties = jo.getAsJsonObject("properties");
            for (Map.Entry<String, JsonElement> entry : jProperties.entrySet()) {
                JsonElement value0 = entry.getValue();
                if (value0.isJsonPrimitive()) {
                    JsonPrimitive value = value0.getAsJsonPrimitive();
                    if (value.isString())
                        properties.put(entry.getKey(), value.getAsString());
                    else if (value.isNumber())
                        properties.put(entry.getKey(), value.getAsNumber());
                    else if (value.isBoolean())
                        properties.put(entry.getKey(), value.getAsBoolean());
                }
            }
            properties = ImmutableMap.copyOf(properties);
        }
        return new Builder().setId(modid).setName(name).setVersion(version).setDescription(description).setUrl(url).setAuthors(authors).setProperties(properties).build();
    }

    public static ModMetadata fromJsonStream(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return fromJson(JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
        }
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public static class Builder {
        protected String group = "";
        protected String id = "";
        protected ComparableVersion version = UNKNOWN_VERSION;
        protected String name = "";
        protected String description = "";
        protected String url = "";
        protected List<String> authors = Collections.emptyList();
        protected String logo = "";
        protected List<ModDependencyEntry> dependency = Collections.emptyList();
        protected Map<String, Object> properties = Collections.emptyMap();

        public static Builder create() {
            return new Builder();
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setVersion(ComparableVersion version) {
            this.version = version;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = new ComparableVersion(version);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setAuthors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder setLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder setProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setDependency(List<ModDependencyEntry> dependency) {
            this.dependency = dependency;
            return this;
        }

        public ModMetadata build() {
            return new ModMetadata(group, id, version, name, description, url, authors, logo, dependency, properties);
        }
    }
}
