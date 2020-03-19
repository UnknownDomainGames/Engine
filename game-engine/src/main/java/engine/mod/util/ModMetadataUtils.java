package engine.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engine.mod.Dependency;
import engine.mod.InstallationType;
import engine.mod.ModMetadata;

import java.util.*;

public class ModMetadataUtils {

    public static final Set<String> VANILLA_KEYS = Set.of("id", "version", "main", "name", "installationType",
            "description", "license", "url", "logo", "authors", "permissions", "dependencies");

    public static JsonObject toJson(ModMetadata descriptor) {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", descriptor.getId());
        jo.addProperty("version", descriptor.getVersion().toString());
        jo.addProperty("main", descriptor.getMainClass());
        jo.addProperty("installationType", descriptor.getInstallationType().name());
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
        for (String permission : descriptor.getPermissions()) {
            ja.add(permission);
        }
        jo.add("permissions", ja);

        ja = new JsonArray();
        for (Dependency dependencyEntry : descriptor.getDependencies()) {
            ja.add(dependencyEntry.toString());
        }
        jo.add("dependencies", ja);

        for (Map.Entry<String, JsonElement> entry : descriptor.getCustomElements().entrySet()) {
            if (VANILLA_KEYS.contains(entry.getKey())) {
                continue;
            }

            jo.add(entry.getKey(), entry.getValue());
        }
        return jo;
    }

    public static ModMetadata fromJson(JsonObject jo) {
        ModMetadata.Builder builder = ModMetadata.builder();
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
        if (jo.has("installationType")) {
            builder.installationType(InstallationType.valueOf(jo.get("installationType").getAsString()));
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
        if (jo.has("permissions")) {
            List<String> permissions = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("permissions")) {
                if (je.isJsonPrimitive()) {
                    permissions.add(je.getAsString());
                }
            }
            builder.permissions(List.copyOf(permissions));
        }
        if (jo.has("dependencies")) {
            List<Dependency> dependencies = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("dependencies")) {
                dependencies.add(Dependency.parse(je.getAsString()));
            }
            builder.dependencies(List.copyOf(dependencies));
        }
        Map<String, JsonElement> elements = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
            if (VANILLA_KEYS.contains(entry.getKey())) {
                continue;
            }
            elements.put(entry.getKey(), entry.getValue());
        }
        builder.elements(Map.copyOf(elements));
        return builder.build();
    }
}
