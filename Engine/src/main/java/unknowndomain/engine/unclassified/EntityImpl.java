package unknowndomain.engine.unclassified;

import unknowndomain.engine.api.unclassified.Prototype;
import unknowndomain.engine.api.unclassified.RuntimeObject;
import unknowndomain.engine.api.unclassified.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityImpl implements Entity {
    private World world;

    EntityImpl(World context) {
        this.world = context;
    }

//    @Override
//    public <Context extends RuntimeObject> Prototype<? extends RuntimeObject, Context> getPrototype() {
//        return null;
//    }

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

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }

    @Override
    public void tick() {

    }
}
