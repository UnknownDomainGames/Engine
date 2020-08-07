package engine.server.player;

import configuration.Config;
import configuration.io.ConfigIOUtils;
import engine.Platform;
import engine.block.component.ActivateBehavior;
import engine.entity.CameraEntity;
import engine.entity.Entity;
import engine.entity.component.TwoHands;
import engine.event.Listener;
import engine.event.block.BlockInteractEvent;
import engine.event.block.cause.BlockInteractCause;
import engine.game.GameServerFullAsync;
import engine.item.component.ActivateBlockBehavior;
import engine.player.Profile;
import engine.server.event.NetworkDisconnectedEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.NetworkHandler;
import engine.server.network.ServerGameplayNetworkHandlerContext;
import engine.server.network.packet.PacketDisconnect;
import engine.server.network.packet.c2s.PacketPlayerAction;
import engine.server.network.packet.c2s.PacketPlayerMove;
import engine.server.network.packet.s2c.PacketBlockUpdate;
import engine.server.network.packet.s2c.PacketGameData;
import engine.server.network.packet.s2c.PacketPlayerPosView;
import engine.util.Side;
import engine.world.WorldCommon;
import engine.world.hit.BlockHitResult;
import org.joml.Vector3d;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class PlayerManager {

    private GameServerFullAsync gameServer;

    protected final Set<ServerPlayer> players = new HashSet<>();

    public PlayerManager(GameServerFullAsync gameServer) {
        this.gameServer = gameServer;
        Platform.getEngine().getEventBus().register(this);
        gameServer.getNetworkServer().getEventBus().register(this);
    }

    public void onPlayerConnect(NetworkHandler networkHandler, ServerPlayer player) {

//        gameServer.joinPlayer(player.getProfile(), player.getControlledEntity());
//        players.add(player);

        String playerAddress = "local";
        if (networkHandler.getRemoteAddress() != null) {
            playerAddress = networkHandler.getRemoteAddress().toString();
        }
        Platform.getLogger().info("{}[{}] joined the server at ({})", player.getProfile().getName(), playerAddress, player.getControlledEntity().getPosition());
        players.add(player);
        networkHandler.sendPacket(new PacketGameData(gameServer.getData()));
        ((WorldCommon) player.getWorld()).getChunkManager().handlePlayerJoin(player);
        handlePlayerPosViewSyncing(player);
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
            var optionalWorld = gameServer.getWorld(config.getString("world"));
            if (optionalWorld.isPresent()) {
                var world = optionalWorld.get();
                var posX = config.getDouble("posX");
                var posY = config.getDouble("posY");
                var posZ = config.getDouble("posZ");
                playerEntity = world.spawnEntity(CameraEntity.class, posX, posY, posZ);
                playerEntity.getRotation().set(config.getFloat("yaw"), config.getFloat("pitch"), 0);
            } else {
                //TODO: find default spawning world
                var o2 = gameServer.getWorlds().stream().findFirst();
                if (o2.isEmpty()) {
                    playerEntity = null;
                } else {
                    var world = o2.get();
                    //TODO: configurable entity type, spawn position
                    playerEntity = world.spawnEntity(CameraEntity.class, 0, 6, 0);
                }
            }
        } else {
            var o2 = gameServer.getWorlds().stream().findFirst();
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
        var path = gameServer.getStoragePath().resolve("player").resolve(profile.getUuid().toString() + ".json");
        if (!path.toFile().exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ConfigIOUtils.load(path));
    }

    public void savePlayerData(ServerPlayer player) {
        if (!player.isControllingEntity()) return;
        var path = gameServer.getStoragePath().resolve("player").resolve(player.getProfile().getUuid().toString() + ".json");
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

    public void saveAllPlayers() {
        for (ServerPlayer player : players) {
            savePlayerData(player);
        }
    }

    public void disconnectAllPlayers() {
        for (ServerPlayer player : players) {
            var reason = "Server close";
            player.getNetworkHandler().sendPacket(new PacketDisconnect(reason), future -> player.getNetworkHandler().closeChannel(reason));
        }
    }

    @Listener
    public void onPlayerDisconnected(NetworkDisconnectedEvent event) {
        if (event.getHandler().getStatus() == ConnectionStatus.GAMEPLAY && event.getHandler().getSide() == Side.SERVER) {
            var player = ((ServerGameplayNetworkHandlerContext) event.getHandler().getContext()).getPlayer();
            savePlayerData(player);
            players.remove(player);
        }
    }

    @Listener
    public void onPlayerMove(PacketReceivedEvent<PacketPlayerMove> event) {
        if (event.getHandler().getStatus() == ConnectionStatus.GAMEPLAY) {
            var player = ((ServerGameplayNetworkHandlerContext) event.getHandler().getContext()).getPlayer();
            if (player.getControlledEntity() != null) {
                if (event.getPacket().hasPositionUpdated()) {
                    var dx = event.getPacket().getLastPosX() - player.getControlledEntity().getPosition().x;
                    var dy = event.getPacket().getLastPosY() - player.getControlledEntity().getPosition().y;
                    var dz = event.getPacket().getLastPosZ() - player.getControlledEntity().getPosition().z;
                    if (dx * dx + dy * dy + dz * dz >= 0.5 * 0.5) { //TODO: adjust the constant to the most suitable one
                        // Client pos and server pos has significant distance, sync back with client
                        handlePlayerPosViewSyncing(player);
                        return;
                    }
                    var posX = event.getPacket().getPosX();
                    var posY = event.getPacket().getPosY();
                    var posZ = event.getPacket().getPosZ();
                    var distX = posX - event.getPacket().getLastPosX();
                    var distY = posY - event.getPacket().getLastPosY();
                    var distZ = posZ - event.getPacket().getLastPosZ();
                    var prev = player.getControlledEntity().getPosition().get(new Vector3d());
                    player.getControlledEntity().getPosition().set(posX, posY, posZ);
                    ((WorldCommon) player.getWorld()).getChunkManager().handlePlayerMove(player, prev);
                }

                if (event.getPacket().hasLookUpdated()) {
                    player.getControlledEntity().getRotation().set(event.getPacket().getYaw(), event.getPacket().getPitch(), 0);
                }
            }
        }
    }

    @Listener
    public void handlePlayerAction(PacketReceivedEvent<PacketPlayerAction> event) {
        if (event.getHandler().getStatus() != ConnectionStatus.GAMEPLAY) {
            Platform.getLogger().error("Receiving PlayerAction Packet while not in gameplay status!");
            return;
        }
        var player = ((ServerGameplayNetworkHandlerContext) event.getHandler().getContext()).getPlayer();
        switch (event.getPacket().getAction()) {
            case START_BREAK_BLOCK:
                break;
            case STOP_BREAK_BLOCK:
                break;
            case INTERRUPTED:
                break;
            case INTERACT_BLOCK:
                var cause = new BlockInteractCause.PlayerCause(player);
                var blockHitResult = ((BlockHitResult) event.getPacket().getHitResult());
                if (blockHitResult instanceof BlockHitResult.Postponed) {
                    blockHitResult = ((BlockHitResult.Postponed) blockHitResult).build(gameServer);
                }
                gameServer.getEngine().getEventBus().post(new BlockInteractEvent.Activate(blockHitResult, cause));
                BlockHitResult finalBlockHitResult = blockHitResult;
                blockHitResult.getBlock().getComponent(ActivateBehavior.class).ifPresent(activateBehavior ->
                        activateBehavior.onActivated(finalBlockHitResult, cause));
                player.getControlledEntity().getComponent(TwoHands.class).ifPresent(twoHands ->
                        twoHands.getMainHand().ifNonEmpty(itemStack ->
                                itemStack.getItem().getComponent(ActivateBlockBehavior.class).ifPresent(activateBlockBehavior ->
                                        activateBlockBehavior.onActivate(itemStack, finalBlockHitResult, cause))));
                event.getHandler().sendPacket(new PacketBlockUpdate(blockHitResult.getWorld(), blockHitResult.getPos()));
                event.getHandler().sendPacket(new PacketBlockUpdate(blockHitResult.getWorld(), blockHitResult.getPos().offset(blockHitResult.getDirection())));
                break;
        }
    }

    public void handlePlayerPosViewSyncing(ServerPlayer player) {
        var networkHandler = player.getNetworkHandler();
        var packet = new PacketPlayerPosView(player.getControlledEntity().getPosition().x, player.getControlledEntity().getPosition().y, player.getControlledEntity().getPosition().z, player.getControlledEntity().getRotation().x, player.getControlledEntity().getRotation().y, 0, new Random().nextInt());
        networkHandler.sendPacket(packet);
    }
}
