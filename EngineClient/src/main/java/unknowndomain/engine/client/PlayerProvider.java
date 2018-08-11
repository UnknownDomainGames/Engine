package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.world.World;

public class PlayerProvider implements Prototype<PlayerClient, World> {
    @Override
    public PlayerClient createObject(GameContext gameContext, World context) {
        return new PlayerClient(null);
    }

//    @Override
//    public List<Action<PlayerClient, World>> getActions() {
//        return null;
//    }
}
