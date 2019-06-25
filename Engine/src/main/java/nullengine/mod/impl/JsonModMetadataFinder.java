package nullengine.mod.impl;

import com.google.gson.JsonObject;
import nullengine.mod.ModMetadata;
import nullengine.mod.ModMetadataFinder;
import nullengine.mod.exception.InvalidModMetadataException;
import nullengine.mod.util.JsonModMetadataUtils;
import nullengine.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
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
    public ModMetadata find(Collection<Path> sources) {
        JsonObject jo = null;

        for (Path source : sources) {
            try {
                if (Files.isDirectory(source)) {
                    try (Reader reader = new InputStreamReader(Files.newInputStream(source.resolve(fileName)))) {
                        jo = JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject();
                    }
                } else {
                    try (JarFile jarFile = new JarFile(source.toFile())) {
                        JarEntry jarEntry = jarFile.getJarEntry(fileName);
                        if (jarEntry == null) {
                            continue;
                        }

                        try (Reader reader = new InputStreamReader(jarFile.getInputStream(jarEntry))) {
                            jo = JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject();
                        }
                    }
                }
            } catch (IOException e) {
                throw new InvalidModMetadataException(sources, e);
            }
        }

        if (jo == null) {
            throw new InvalidModMetadataException(sources);
        }

        if (!jo.has("id")) {
            throw new InvalidModMetadataException(String.format("\"Invalid mod metadata. Missing \"id\". Sources: [%s]", StringUtils.join(sources, ",")));
        }

        if (!jo.has("mainClass")) {
            throw new InvalidModMetadataException(String.format("\"Invalid mod metadata. Missing \"mainClass\". Sources: [%s]", StringUtils.join(sources, ",")));
        }

        return JsonModMetadataUtils.fromJson(jo);
    }
}
