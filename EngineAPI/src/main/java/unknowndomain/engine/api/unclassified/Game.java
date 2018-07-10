package unknowndomain.engine.api.unclassified;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * maybe act like the server....
 */
public class Game implements RuntimeEntity<Game>, FlyweightObject<RuntimeEntity<Game>, Game> {
    @Override
    public FlyweightObject<RuntimeEntity<Game>, Game> getDefinition() {
        return this;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public void onCreate(@Nonnull Game game) {

    }

    @Override
    public void onDestroy(@Nonnull Game game) {

    }

    @Override
    public RuntimeEntity<Game> createEntity(Game context) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getModule(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public void putModule(@Nonnull Object module) {

    }

    @Override
    public <T> void unloadModule(@Nonnull Class<T> type) {

    }
}
