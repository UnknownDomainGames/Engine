package unknowndomain.engine.unclassified;

import unknowndomain.engine.api.unclassified.World;

public class EntityProviderImpl implements EntityProvider {
    @Override
    public Entity createObject(World context) {
        return new EntityImpl(context);
    }
}
