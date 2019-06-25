package nullengine.mod;

import nullengine.util.versioning.Version;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface ModMetadata {

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
