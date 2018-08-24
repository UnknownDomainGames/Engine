package unknowndomain.engine.mod.source;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class DirModSource implements ModSource {

    private final Path dir;

    private Map<String, LoadableMod> loadableMods;

    public DirModSource(Path dir) {
        Validate.notNull(dir);
        if (!Files.exists(dir)) {
            throw new IllegalArgumentException(dir.toAbsolutePath() + " don't exist.");
        }
        if (!Files.isDirectory(dir))
            throw new IllegalArgumentException(dir.toAbsolutePath() + " isn't a directory.");
        this.dir = dir;
        refresh();
    }

    @Override
    public Collection<LoadableMod> getLoadableMods() {
        return loadableMods.values();
    }

    @Override
    public LoadableMod getLoadableMod(String modId) {
        return loadableMods.get(modId);
    }

    @Override
    public boolean hasLoadableMod(String modId) {
        return loadableMods.containsKey(modId);
    }

    @Override
    public void refresh() {
        Map<String, LoadableMod> loadableMods = new HashMap<>();
        try {
            Files.walk(dir, 1).filter(file -> file.getFileName().toString().endsWith(".jar")).forEach(file -> {
                try (JarFile jarFile = new JarFile(file.toFile())) {
                    ZipEntry entry = jarFile.getEntry("metadata.json");
                    if (entry == null) {
                        Platform.getLogger().warn("metadata.json isn't exists. Path: " + file.toAbsolutePath().toString());
                        return;
                    }
                    ModMetadata metadata = ModMetadata.fromJsonStream(jarFile.getInputStream(entry));
                    loadableMods.put(metadata.getModId(), new LoadableMod(file, metadata));
                } catch (IOException | ModDependencyException e) {
                    Platform.getLogger().warn(e.getMessage(), e); // TODO: warn
                }
            });
        } catch (IOException e) {
            throw new ModSourceRefreshException(e);
        }
        this.loadableMods = ImmutableMap.copyOf(loadableMods);
    }
}
