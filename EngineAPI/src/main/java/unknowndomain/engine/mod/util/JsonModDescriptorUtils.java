package unknowndomain.engine.mod.util;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModDescriptor;

import java.util.Map;

public class JsonModDescriptorUtils {

    public static JsonObject toJson(ModDescriptor descriptor) {
        JsonObject jo = new JsonObject();
        jo.addProperty("modId", descriptor.getModId());
        jo.addProperty("version", descriptor.getVersion().toString());
        jo.addProperty("mainClass", descriptor.getMainClass());

        if (!Strings.isNullOrEmpty(descriptor.getName())) {
            jo.addProperty("name", descriptor.getName());
        }

        if (!Strings.isNullOrEmpty(descriptor.getDescription())) {
            jo.addProperty("description", descriptor.getDescription());
        }

        if (!Strings.isNullOrEmpty(descriptor.getUrl())) {
            jo.addProperty("url", descriptor.getUrl());
        }

        if (!Strings.isNullOrEmpty(descriptor.getLogoFile())) {
            jo.addProperty("logoFile", descriptor.getLogoFile());
        }

        JsonArray ja = new JsonArray();
        for (String author : descriptor.getAuthors()) {
            ja.add(author);
        }
        if (ja.size() != 0) {
            jo.add("authors", ja);
        }

        ja = new JsonArray();
        for (ModDependencyEntry dependencyEntry : descriptor.getDependencies()) {
            ja.add(dependencyEntry.toString());
        }
        if (ja.size() != 0) {
            jo.add("dependencies", ja);
        }

        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> entry : descriptor.getProperties().entrySet()) {
            properties.addProperty(entry.getKey(), entry.getValue());
        }
        if (properties.size() != 0) {
            jo.add("properties", properties);
        }
        return jo;
    }
}
