package engine.server.network.packet.s2c;

import configuration.Config;
import engine.game.GameData;
import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;

import java.io.IOException;
import java.util.HashMap;

public class PacketGameData implements Packet {

    private String playerWorldLocation;
    private Config gameDataConfig;

    public PacketGameData() {

    }

    public PacketGameData(GameData gameData, String name) {
        gameDataConfig = new Config();
        gameDataConfig.set("Name", gameData.getName());
        gameDataConfig.set("Created", gameData.isCreated());
        gameDataConfig.set("Worlds", gameData.getWorlds());
        gameDataConfig.set("Dependencies", gameData.getDependencies());
        gameDataConfig.set("Registries", gameData.getRegistries());
        this.playerWorldLocation = name;
    }


    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(playerWorldLocation);
        buf.writeString(gameDataConfig.getString("Name"));
        buf.writeBoolean(gameDataConfig.getBoolean("Created"));
        var worlds = gameDataConfig.getMap("Worlds");
        buf.writeVarInt(worlds.size());
        worlds.forEach((key, value) -> {
            buf.writeString(key);
            buf.writeString((String) value);
        });
        var dependencies = gameDataConfig.getMap("Dependencies");
        buf.writeVarInt(dependencies.size());
        dependencies.forEach((key, value) -> {
            buf.writeString(key);
            buf.writeString((String) value);
        });
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        playerWorldLocation = buf.readString();
        gameDataConfig = new Config();
        gameDataConfig.set("Name", buf.readString());
        gameDataConfig.set("Created", buf.readBoolean());
        var size = buf.readVarInt();
        var worlds = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            worlds.put(buf.readString(), buf.readString());
        }
        gameDataConfig.set("Worlds", worlds);
        size = buf.readVarInt();
        var de = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            de.put(buf.readString(), buf.readString());
        }
        gameDataConfig.set("Dependencies", de);
    }

    public Config getGameDataConfig() {
        return gameDataConfig;
    }

    public String getPlayerWorldLocation() {
        return playerWorldLocation;
    }
}
