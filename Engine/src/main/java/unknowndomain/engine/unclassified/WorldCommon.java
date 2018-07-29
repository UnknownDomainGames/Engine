package unknowndomain.engine.unclassified;

import unknowndomain.engine.api.unclassified.Game;
import unknowndomain.engine.api.unclassified.World;
import unknowndomain.engine.api.unclassified.WorldProvider;

public class WorldCommon implements WorldProvider {
    @Override
    public World createObject(Game context) {
        return new WorldImpl(context);
    }
}
