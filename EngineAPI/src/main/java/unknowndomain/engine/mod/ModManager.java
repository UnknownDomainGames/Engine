package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.ModAlreadyLoadedExeception;
import unknowndomain.engine.mod.exception.ModLoadException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public interface ModManager {

    ModContainer loadMod(Path path) throws ModLoadException, ModAlreadyLoadedExeception;

    ModContainer loadMod(ModDescriptor modDescriptor) throws ModLoadException, ModAlreadyLoadedExeception;

    Collection<ModContainer> loadMod(Iterator<Path> pathIterator) throws ModLoadException, ModAlreadyLoadedExeception;

    @Nullable
    ModContainer getMod(String modId);

    @Nullable
    ModContainer getMod(Class<?> clazz);

    boolean isModLoaded(String modId);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
