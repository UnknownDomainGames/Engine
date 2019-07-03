package nullengine.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.mod.ModDependencyItem;
import nullengine.mod.ModMetadata;
import nullengine.mod.misc.SimpleModMetadata;

import java.util.*;

public class ModMetadataUtils {

    public static final Set<String> VANILLA_KEYS = Set.of("id", "version", "main", "name",
            "description", "license", "url", "logo", "authors", "dependencies", "properties");

    public static JsonObject toJson(ModMetadata descriptor) {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", descriptor.getId());
        jo.addProperty("version", descriptor.getVersion().toString());
        jo.addProperty("main", descriptor.getMainClass());
        jo.addProperty("name", descriptor.getName());
        jo.addProperty("description", descriptor.getDescription());
        jo.addProperty("license", descriptor.getLicense());
        jo.addProperty("url", descriptor.getUrl());
        jo.addProperty("logo", descriptor.getLogoFile());

        JsonArray ja = new JsonArray();
        for (String author : descriptor.getAuthors()) {
            ja.add(author);
        }
        jo.add("authors", ja);

        ja = new JsonArray();
        for (ModDependencyItem dependencyEntry : descriptor.getDependencies()) {
            ja.add(dependencyEntry.toString());
        }
        jo.add("dependencies", ja);

        JsonObject properties = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : descriptor.getProperties().entrySet()) {
            properties.add(entry.getKey(), entry.getValue());
        }
        jo.add("properties", properties);
        return jo;
    }

    public static ModMetadata fromJson(JsonObject jo) {
        SimpleModMetadata.Builder builder = SimpleModMetadata.builder();

        if (jo.has("id")) {
            builder.id(jo.get("id").getAsString());
        }

        if (jo.has("main")) {
            builder.mainClass(jo.get("main").getAsString());
        }

        if (jo.has("version")) {
            builder.version(jo.get("version").getAsString());
        }
        if (jo.has("name")) {
            builder.name(jo.get("name").getAsString());
        }
        if (jo.has("description")) {
            builder.description(jo.get("description").getAsString());
        }
        if (jo.has("license")) {
            builder.license(jo.get("license").getAsString());
        }
        if (jo.has("url")) {
            builder.url(jo.get("url").getAsString());
        }
        if (jo.has("logo")) {
            builder.logo(jo.get("logo").getAsString());
        }
        if (jo.has("authors")) {
            List<String> authors = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("authors")) {
                if (je.isJsonPrimitive()) {
                    authors.add(je.getAsString());
                }
            }
            builder.authors(List.copyOf(authors));
        }
        if (jo.has("dependencies")) {
            List<ModDependencyItem> dependencies = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("dependencies")) {
                dependencies.add(ModDependencyItem.parse(je.getAsString()));
            }
            builder.dependencies(List.copyOf(dependencies));
        }
        if (jo.has("properties")) {
            Map<String, JsonElement> properties = new HashMap<>();
            JsonObject jProperties = jo.getAsJsonObject("properties");
            for (Map.Entry<String, JsonElement> entry : jProperties.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
            builder.properties(Map.copyOf(properties));
        }
        return builder.build();
    }
}
