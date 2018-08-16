package unknowndomain.engine.mod.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.io.IOUtils;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileModMetadata implements ModMetadata {

    public static FileModMetadata create(InputStream inputStream) {
        try {
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
            return new FileModMetadata(jsonObject);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private String name;
    private ComparableVersion version;
    private String description;
    private String url;
    private List<String> authors = Collections.emptyList();
    private String logoFile;
    private Map<String, Object> properties;

    private FileModMetadata(JsonObject jo) {
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
            List<String> authors = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("authors")) {
                if (je.isJsonPrimitive()) {
                    authors.add(je.getAsString());
                }
            }
            authors = ImmutableList.copyOf(authors);
        }
        if (jo.has("properties")) {
            Map<String, Object> properties = new HashMap<>();
            JsonObject jProperties = jo.getAsJsonObject("properties");
            for(Map.Entry<String, JsonElement> entry : jProperties.entrySet()) {
                JsonElement value0 = entry.getValue();
                if(value0.isJsonPrimitive()) {
                    JsonPrimitive value = value0.getAsJsonPrimitive();
                    if(value.isString())
                        properties.put(entry.getKey(), value.getAsString());
                    else if(value.isNumber())
                        properties.put(entry.getKey(), value.getAsNumber());
                    else if(value.isBoolean())
                        properties.put(entry.getKey(), value.getAsBoolean());
                }
            }
            properties = ImmutableMap.copyOf(properties);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ComparableVersion getVersion() {
        return version;
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
    public Map<String, Object> getProperties() {
        return properties;
    }
}
