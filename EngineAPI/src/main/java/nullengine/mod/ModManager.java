package nullengine.mod;

import nullengine.mod.exception.ModAlreadyLoadedException;
import nullengine.mod.exception.ModLoadException;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface ModManager {

    @Nonnull
    ModContainer loadMod(Path path) throws ModLoadException, ModAlreadyLoadedException;

    Optional<ModContainer> getMod(String modId);

    Optional<ModContainer> getMod(Class<?> clazz);

    boolean isModLoaded(String modId);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
