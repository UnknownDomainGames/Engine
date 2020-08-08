package engine.server.network;

import engine.server.player.ServerPlayer;
import engine.util.Side;

public class ServerGameplayNetworkHandlerContext implements NetworkHandlerContext {
    private ServerPlayer player;

    public ServerGameplayNetworkHandlerContext(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public Side getContextSide() {
        return Side.SERVER;
    }

    @Override
    public boolean isSideDepends() {
        return true;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return ConnectionStatus.GAMEPLAY;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
