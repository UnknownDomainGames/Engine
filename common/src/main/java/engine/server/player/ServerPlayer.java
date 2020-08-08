package engine.server.player;

import engine.entity.Entity;
import engine.player.PlayerImpl;
import engine.player.Profile;
import engine.server.network.NetworkHandler;

public class ServerPlayer extends PlayerImpl {
    public ServerPlayer(Profile profile, Entity controlledEntity) {
        super(profile, controlledEntity);
    }

    public ServerPlayer(Profile profile, NetworkHandler handler, Entity controlledEntity) {
        super(profile, handler, controlledEntity);
    }
}
