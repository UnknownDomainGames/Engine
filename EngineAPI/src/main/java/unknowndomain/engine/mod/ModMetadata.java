package unknowndomain.engine.mod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.util.*;

public class ModMetadata {
    private String modid;
    private String name;
    private ComparableVersion version;
    private String description;
    private String url;
    private List<String> authors;
    private String logoFile;
    private Map<String, Object> properties;

    public ModMetadata(String modid, String name, ComparableVersion version, String description, String url, List<String> authors, String logoFile, Map<String, Object> properties) {
        this.modid = modid;
        this.name = name;
        this.version = version;
        this.description = description;
        this.url = url;
        this.authors = authors;
        this.logoFile = logoFile;
        this.properties = properties;
    }

    public static ModMetadata fromJson(JsonObject jo) {
        String modid = "", name = "", description = "", url = "", logoFile = "";
        ComparableVersion version = null;
        List<String> authors = Collections.emptyList();
        Map<String, Object> properties = Collections.emptyMap();

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
        return new ModMetadata(modid, name, version, description, url, authors, logoFile, properties);
    }

    public String getModid() {
        return modid;
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

    public Map<String, Object> getProperties() {
        return properties;
    }
}
