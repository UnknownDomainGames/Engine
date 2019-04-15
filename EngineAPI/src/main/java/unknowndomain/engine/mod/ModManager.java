package unknowndomain.engine.mod;

import unknowndomain.engine.mod.exception.ModAlreadyLoadedException;
import unknowndomain.engine.mod.exception.ModLoadException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public interface ModManager {

    ModContainer loadMod(Path path) throws ModLoadException, ModAlreadyLoadedException;

    ModContainer loadMod(ModDescriptor modDescriptor) throws ModLoadException, ModAlreadyLoadedException;

    Collection<ModContainer> loadMod(Iterator<Path> pathIterator) throws ModLoadException, ModAlreadyLoadedException;

    @Nullable
    ModContainer getMod(String modId);

    @Nullable
    ModContainer getMod(Class<?> clazz);

    boolean isModLoaded(String modId);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
