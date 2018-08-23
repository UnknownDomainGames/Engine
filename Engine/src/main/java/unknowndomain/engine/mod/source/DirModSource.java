package unknowndomain.engine.mod.source;

import com.google.common.collect.ImmutableList;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.ModIdentity;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModSource;
import unknowndomain.engine.mod.ModSourceRefreshException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class DirModSource implements ModSource {

    private final Path dir;

    private List<ModMetadata> loadableMods;

    public DirModSource(Path dir) {
        this.dir = dir;
    }

    @Override
    public List<ModMetadata> getLoadableMods() {
        return loadableMods;
    }

    @Override
    public Path getModPath(ModIdentity modId) {
        return null;
    }

    @Override
    public void refresh() {
        List<ModMetadata> loadableMods = new ArrayList<>();
        try {
            Files.walk(dir, 1).filter(file -> file.getFileName().toString().endsWith(".jar")).forEach(file -> {
                try (JarFile jarFile = new JarFile(file.toFile())) {
                    ZipEntry entry = jarFile.getEntry("metadata.json");
                    if (entry == null) {
                        Platform.getLogger().warn(""); // TODO: warn
                        return;
                    }
                    ModMetadata metadata = ModMetadata.fromJsonStream(jarFile.getInputStream(entry));
                    loadableMods.add(metadata);
                } catch (IOException e) {
                    Platform.getLogger().warn(e.getMessage(), e); // TODO: warn
                }
            });
        } catch (IOException e) {
            throw new ModSourceRefreshException(e);
        }
        this.loadableMods = ImmutableList.copyOf(loadableMods);
    }
}
