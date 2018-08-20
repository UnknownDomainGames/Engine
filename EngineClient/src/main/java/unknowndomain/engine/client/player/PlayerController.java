package unknowndomain.engine.client.player;

import unknowndomain.engine.action.Action;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.entity.Player;

import java.util.List;

public abstract class PlayerController {
    protected Camera camera;
    protected Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void tick() {
    }

    public abstract List<Action> getActions();
}
