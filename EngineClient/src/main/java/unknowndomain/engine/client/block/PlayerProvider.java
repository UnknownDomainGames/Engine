package unknowndomain.engine.client.block;

import unknowndomain.engine.Prototype;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.world.World;

import java.util.List;

public class PlayerProvider implements Prototype<Player, World> {
    @Override
    public Player createObject(GameContext gameContext, World context) {
        return new Player(null);
    }

//    @Override
//    public List<Action<Player, World>> getActions() {
//        return null;
//    }
}
