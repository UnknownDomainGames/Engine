package unknowndomain.engine.mod;

import unknowndomain.engine.util.JsonUtils;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;


public class ModMetadata { // TODO generate a builder for this
    private final String modId;
    private final String name;
    private final ComparableVersion version;
    private final String description;
    private final String url;
    private final List<String> authors;
    private final String logoFile;
    private final List<ModDependencyEntry> dependencies;
    private final Map<String, Object> properties;

    public ModMetadata(String modId, String name, ComparableVersion version, String description, String url,
                       List<String> authors, String logoFile, List<ModDependencyEntry> dependencies, Map<String, Object> properties) {
        this.modId = modId;
        this.name = name;
        this.version = version;
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
        ComparableVersion version = null;
        // TODO make default and validate modId & version in metadata

        if (jo.has("modid")) {
            modid = jo.get("modid").getAsString();
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
                dependencies.add(ModDependencyEntry.create(je.getAsString()));
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
        return new ModMetadata(modid, name, version, description, url, authors, logoFile, dependencies, properties);
    }

    public static ModMetadata fromJsonStream(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return fromJson(JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
        }
    }

    public String getModId() {
        return modId;
    }

    public String getName() {
        return name;
    }

    public ComparableVersion getVersion() {
        return version;
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
}
