package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public interface ModManager {

    @Nonnull
    ModContainer loadMod(Path path) throws ModLoadException, ModAlreadyLoadedException;

    @Nonnull
    ModContainer loadMod(ModDescriptor modDescriptor) throws ModLoadException, ModAlreadyLoadedException;

    @Nonnull
    Collection<ModContainer> loadMod(Iterator<Path> pathIterator) throws ModLoadException, ModAlreadyLoadedException;

    Optional<ModContainer> getMod(String modId);

    Optional<ModContainer> getMod(Class<?> clazz);

    boolean isModLoaded(String modId);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
