package unknowndomain.engine.mod;

import unknowndomain.engine.game.Game;
import unknowndomain.engine.util.Owner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * The in-game mod manager. Not the one who manage the mod sources.
 */
@Owner(Game.class)
public interface ModManager {
    /**
     * Find the mod by modid.
     *
     * @return The mod container, might be null
     */
    @Nullable
    ModContainer findMod(String modId);

    /**
     * Find the mod by its type.
     */
    @Nullable
    ModContainer findMod(Class<?> clazz);

    /**
     * Is the mod loaded.
     */
    boolean isModLoaded(String modId);

    @Nonnull
    Collection<ModContainer> getLoadedMods();
}
