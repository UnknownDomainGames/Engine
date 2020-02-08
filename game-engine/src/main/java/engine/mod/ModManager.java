package engine.mod;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface ModManager {

    Optional<ModContainer> getMod(String id);

    Optional<ModContainer> getMod(Class<?> clazz);

    boolean isModLoaded(String id);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
