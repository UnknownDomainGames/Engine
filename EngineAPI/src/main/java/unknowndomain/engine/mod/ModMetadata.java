package unknowndomain.engine.mod;

import unknowndomain.engine.util.versioning.Version;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface ModMetadata {

    @Nonnull
    Path getSource();

    @Nonnull
    String getId();

    @Nonnull
    Version getVersion();

    String getMainClass();

    String getName();

    String getDescription();

    String getLicense();

    String getUrl();

    List<String> getAuthors();

    String getLogoFile();

    List<ModDependencyEntry> getDependencies();

    Map<String, String> getProperties();
}
