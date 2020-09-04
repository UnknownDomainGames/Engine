package engine.game;

import configuration.Config;
import engine.server.network.packet.s2c.PacketGameData;

import java.nio.file.Path;

/**
 * A GameData variant received from server side
 */
public class MultiplayerGameData extends GameData {
    protected MultiplayerGameData(Config gameData) {
        super(Path.of(""), gameData);
    }

    public static MultiplayerGameData fromPacket(PacketGameData packet) {
        return new MultiplayerGameData(packet.getGameDataConfig());
    }

    @Override
    public void save() {
        // Left blank in purpose
    }
}
