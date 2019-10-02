package nullengine.server.network.packet;

import nullengine.player.Profile;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PacketLoginStart implements Packet {

    private Profile profile;

    @Override
    public void write(PacketBuf buf) throws IOException {
        var uuid = profile.getUuid();
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        String name = profile.getName();
        buf.writeVarInt(name.length());
        buf.writeCharSequence(name, UTF_8);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        profile = new Profile(new UUID(buf.readLong(), buf.readLong()), buf.readCharSequence(buf.readVarInt(), UTF_8).toString());
    }
}
