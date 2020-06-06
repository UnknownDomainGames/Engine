package engine.server.player;

import configuration.Config;
import configuration.io.ConfigIOUtils;
import engine.Platform;
import engine.entity.CameraEntity;
import engine.entity.Entity;
import engine.game.ServerGame;
import engine.player.Profile;
import engine.server.network.NetworkHandler;
import engine.server.network.packet.PacketGameData;
import engine.world.WorldCommon;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class PlayerManager {

    private ServerGame game;

    protected final Set<ServerPlayer> players = new HashSet<>();

    public PlayerManager(ServerGame game) {
        this.game = game;
    }

    public void onPlayerConnect(NetworkHandler networkHandler, ServerPlayer player) {
        String playerAddress = "local";
        if (networkHandler.getRemoteAddress() != null) {
            playerAddress = networkHandler.getRemoteAddress().toString();
        }
        Platform.getLogger().info("{}[{}] joined the server at ({})", player.getProfile().getName(), playerAddress, player.getControlledEntity().getPosition());
        players.add(player);
        networkHandler.sendPacket(new PacketGameData(game.getData()));
        ((WorldCommon) player.getWorld()).getChunkManager().handlePlayerJoin(player);
    }

    public void joinPlayer(ServerPlayer player) {
        players.add(player);
    }

    public ServerPlayer createPlayer(NetworkHandler networkHandler, Profile profile) {
        var uuid = profile.getUuid();
        //TODO: load player data
        Entity playerEntity;
        var optionalConfig = loadPlayerData(profile);
        if (optionalConfig.isPresent()) {
            var config = optionalConfig.get();
            var optionalWorld = game.getWorld(config.getString("world"));
            if (optionalWorld.isPresent()) {
                var world = optionalWorld.get();
                var posX = config.getDouble("posX");
                var posY = config.getDouble("posY");
                var posZ = config.getDouble("posZ");
                playerEntity = world.spawnEntity(CameraEntity.class, posX, posY, posZ);
                playerEntity.getRotation().set(config.getFloat("yaw"), config.getFloat("pitch"), 0);
            } else {
                //TODO: find default spawning world
                var o2 = game.getWorlds().stream().findFirst();
                if (o2.isEmpty()) {
                    playerEntity = null;
                } else {
                    var world = o2.get();
                    //TODO: configurable entity type, spawn position
                    playerEntity = world.spawnEntity(CameraEntity.class, 0, 6, 0);
                }
            }
        } else {
            var o2 = game.getWorlds().stream().findFirst();
            if (o2.isEmpty()) {
                playerEntity = null;
            } else {
                var world = o2.get();
                //TODO: configurable entity type, spawn position
                playerEntity = world.spawnEntity(CameraEntity.class, 0, 6, 0);
            }
        }
        return new ServerPlayer(profile, networkHandler, playerEntity);
    }

    public Set<ServerPlayer> getPlayer() {
        return players;
    }

    public Optional<Config> loadPlayerData(Profile profile) {
        var path = game.getStoragePath().resolve("player").resolve(profile.getUuid().toString() + ".json");
        if (!path.toFile().exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ConfigIOUtils.load(path));
    }

    public void savePlayerData(ServerPlayer player) {
        if (!player.isControllingEntity()) return;
        var path = game.getStoragePath().resolve("player").resolve(player.getProfile().getUuid().toString() + ".json");
        var config = new Config();
        config.set("world", player.getWorld().getName());
        var position = player.getControlledEntity().getPosition();
        config.set("cachedName", player.getProfile().getName());
        config.set("posX", position.x);
        config.set("posY", position.y);
        config.set("posZ", position.z);
        config.set("yaw", player.getControlledEntity().getRotation().x);
        config.set("pitch", player.getControlledEntity().getRotation().y);
        config.save(path);
    }
}
