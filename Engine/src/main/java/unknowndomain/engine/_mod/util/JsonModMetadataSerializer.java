package unknowndomain.engine._mod.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import unknowndomain.engine._mod.ModDependencyEntry;
import unknowndomain.engine._mod.ModMetadata;
import unknowndomain.engine.util.JsonUtils;
import unknowndomain.engine.util.versioning.ComparableVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import static unknowndomain.engine.mod.ModMetadata.UNKNOWN_VERSION;

public class JsonModMetadataSerializer {

    public static ModMetadata fromJsonStream(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return fromJson(JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
        }
    }

    public static ModMetadata fromJson(JsonObject jo) {
        String modid = "", mainClass = "", name = "", url = "", description = "", logoFile = "";
        List<String> authors = Collections.emptyList();
        List<ModDependencyEntry> dependencies = Collections.emptyList();
        Map<String, String> properties = Collections.emptyMap();
        ComparableVersion version = UNKNOWN_VERSION;
        // TODO make default and validate id & version in metadata

        if (jo.has("id")) {
            modid = jo.get("id").getAsString();
        }
        if (jo.has("version")) {
            version = new ComparableVersion(jo.get("version").getAsString());
        }
        if (jo.has("mainClass")) {
            mainClass = jo.get("mainClass").getAsString();
        }
        if (jo.has("name")) {
            name = jo.get("name").getAsString();
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
                properties.put(entry.getKey(), entry.getValue().getAsString());
            }
            properties = ImmutableMap.copyOf(properties);
        }
        return new ModMetadata.Builder().id(modid).name(name).version(version).mainClass(mainClass).description(description).url(url).logo(logoFile).authors(authors).properties(properties).build();
    }

    public static JsonObject toJson(ModMetadata metadata) {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", metadata.getId());
        jo.addProperty("version", metadata.getVersion().toString());
        jo.addProperty("mainClass", metadata.getMainClass());

        if (!Strings.isNullOrEmpty(metadata.getName())) {
            jo.addProperty("name", metadata.getName());
        }

        if (!Strings.isNullOrEmpty(metadata.getDescription())) {
            jo.addProperty("description", metadata.getDescription());
        }

        if (!Strings.isNullOrEmpty(metadata.getUrl())) {
            jo.addProperty("url", metadata.getUrl());
        }

        if (!Strings.isNullOrEmpty(metadata.getLogoFile())) {
            jo.addProperty("logoFile", metadata.getLogoFile());
        }

        JsonArray ja = new JsonArray();
        for (String author : metadata.getAuthors()) {
            ja.add(author);
        }
        if (ja.size() != 0) {
            jo.add("authors", ja);
        }

        ja = new JsonArray();
        for (ModDependencyEntry dependencyEntry : metadata.getDependencies()) {
            ja.add(dependencyEntry.toString());
        }
        if (ja.size() != 0) {
            jo.add("dependencies", ja);
        }

        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> entry : metadata.getProperties().entrySet()) {
            properties.addProperty(entry.getKey(), entry.getValue());
        }
        if (properties.size() != 0) {
            jo.add("properties", properties);
        }
        return jo;
    }
}
