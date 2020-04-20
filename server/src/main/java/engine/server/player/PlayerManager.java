package engine.server.player;

import engine.Platform;
import engine.game.GameServerFullAsync;
import engine.player.Player;
import engine.player.Profile;
import engine.server.network.NetworkHandler;

import java.util.HashSet;
import java.util.Set;

public class PlayerManager {

    private GameServerFullAsync gameServer;

    protected final Set<Player> players = new HashSet<>();

    public PlayerManager(GameServerFullAsync gameServer) {
        this.gameServer = gameServer;
    }

    public void onPlayerConnect(NetworkHandler networkHandler, ServerPlayer player) {


        gameServer.joinPlayer(player.getProfile(), player.getControlledEntity());

        String playerAddress = "local";
        if (networkHandler.getRemoteAddress() != null) {
            playerAddress = networkHandler.getRemoteAddress().toString();
        }
        Platform.getLogger().info("{}[{}] joined the server at ({})", player.getProfile().getName(), playerAddress, player.getControlledEntity().getPosition());
        players.add(player);
    }

    public ServerPlayer createPlayer(NetworkHandler networkHandler, Profile profile) {
        var uuid = profile.getUuid();

        return new ServerPlayer(profile, networkHandler, null);
    }
}
