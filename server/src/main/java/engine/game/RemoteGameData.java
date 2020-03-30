package engine.game;

import configuration.Config;
import engine.server.network.packet.PacketGameData;

import java.nio.file.Path;

/**
 * A GameData variant received from server side
 */
public class RemoteGameData extends GameData {
    protected RemoteGameData(Config gameData) {
        super(Path.of(""), gameData);
    }

    public static RemoteGameData fromPacket(PacketGameData packet) {
        return new RemoteGameData(packet.getGameDataConfig());
    }

    @Override
    public void save() {
        // Left blank in purpose
    }
}
