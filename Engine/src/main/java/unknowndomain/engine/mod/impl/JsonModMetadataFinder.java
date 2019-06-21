package unknowndomain.engine.mod.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModMetadataFinder;
import unknowndomain.engine.mod.exception.InvalidModDescriptorException;
import unknowndomain.engine.mod.misc.DefaultModMetadata;
import unknowndomain.engine.util.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JsonModMetadataFinder implements ModMetadataFinder {

    private final String fileName;

    public JsonModMetadataFinder() {
        this("metadata.json");
    }

    public JsonModMetadataFinder(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ModMetadata find(Path path) {
        JsonObject jo;

        try {
            if (Files.isDirectory(path)) {
                try (Reader reader = new InputStreamReader(Files.newInputStream(path.resolve(fileName)))) {
                    jo = JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject();
                }
            } else {
                try (JarFile jarFile = new JarFile(path.toFile())) {
                    JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    if (jarEntry == null) {
                        throw new InvalidModDescriptorException(path);
                    }

                    try (Reader reader = new InputStreamReader(jarFile.getInputStream(jarEntry))) {
                        jo = JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) {
            throw new InvalidModDescriptorException(path, e);
        }

        DefaultModMetadata.Builder builder = DefaultModMetadata.builder().source(path);

        if (!jo.has("id")) {
            throw new InvalidModDescriptorException(String.format("\"Invalid mod descriptor. Missing \"id\". Source: %s", path.toAbsolutePath()));
        }

        if (!jo.has("mainClass")) {
            throw new InvalidModDescriptorException(String.format("\"Invalid mod descriptor. Missing \"mainClass\". Source: %s", path.toAbsolutePath()));
        }

        builder.id(jo.get("id").getAsString());
        builder.mainClass(jo.get("mainClass").getAsString());

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
            List<ModDependencyEntry> dependencies = new ArrayList<>();
            for (JsonElement je : jo.getAsJsonArray("dependencies")) {
                dependencies.add(ModDependencyEntry.parse(je.getAsString()));
            }
            builder.dependencies(List.copyOf(dependencies));
        }
        if (jo.has("properties")) {
            Map<String, String> properties = new HashMap<>();
            JsonObject jProperties = jo.getAsJsonObject("properties");
            for (Map.Entry<String, JsonElement> entry : jProperties.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().getAsString());
            }
            builder.properties(Map.copyOf(properties));
        }
        return builder.build();
    }
}
