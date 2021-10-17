package engine.mod.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import engine.mod.ModMetadata;
import engine.mod.exception.InvalidModMetadataException;
import engine.mod.metadata.ModMetadataUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModMetadataFinder {

    private final String fileName;

    public ModMetadataFinder() {
        this(ModMetadataUtils.METADATA_NAME);
    }

    public ModMetadataFinder(String fileName) {
        this.fileName = fileName;
    }

    public ModMetadata find(Collection<Path> sources) {
        JsonObject jo = null;

        for (Path source : sources) {
            if (Files.isDirectory(source)) {
                Path metadataFile = source.resolve(fileName);
                if (Files.notExists(metadataFile)) continue;
                try (Reader reader = Files.newBufferedReader(metadataFile)) {
                    jo = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (IOException e) {
                    throw new InvalidModMetadataException(sources, e);
                }
            } else {
                try (JarFile jarFile = new JarFile(source.toFile())) {
                    JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    if (jarEntry == null) {
                        continue;
                    }

                    try (Reader reader = new InputStreamReader(jarFile.getInputStream(jarEntry))) {
                        jo = JsonParser.parseReader(reader).getAsJsonObject();
                    }
                } catch (IOException e) {
                    throw new InvalidModMetadataException(sources, e);
                }
            }

            if (jo != null) {
                break;
            }
        }

        if (jo == null) {
            throw new InvalidModMetadataException(sources);
        }

        if (!jo.has("id") || jo.get("id").getAsString().isEmpty()) {
            throw new InvalidModMetadataException(String.format("\"Invalid mod metadata. Missing \"id\". Sources: [%s]", StringUtils.join(sources, ",")));
        }

        if (!jo.has("version") || jo.get("version").getAsString().isEmpty()) {
            throw new InvalidModMetadataException(String.format("\"Invalid mod metadata. Missing \"version\". Sources: [%s]", StringUtils.join(sources, ",")));
        }

        if (!jo.has("main") || jo.get("main").getAsString().isEmpty()) {
            throw new InvalidModMetadataException(String.format("\"Invalid mod metadata. Missing \"main\". Sources: [%s]", StringUtils.join(sources, ",")));
        }

        return ModMetadataUtils.fromJson(jo);
    }
}
