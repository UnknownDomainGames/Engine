package unknowndomain.engine.client.input.controller;

import unknowndomain.engine.player.Player;

public abstract class EntityController {

    private final Player player;

    public EntityController(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void handleMotion(MotionType motionType, boolean state);

    public abstract void handleCursorMove(double x, double y);
}
