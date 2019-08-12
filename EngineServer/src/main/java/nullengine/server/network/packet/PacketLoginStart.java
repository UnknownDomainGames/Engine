package nullengine.server.network.packet;

import nullengine.player.Profile;
import nullengine.server.network.PacketBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketLoginStart implements Packet {

    private Profile profile;

    @Override
    public void write(PacketBuf buf) throws IOException {
        var s = profile.uuid.toString();
        buf.writeVarInt(s.length());
        buf.writeCharSequence(s, StandardCharsets.UTF_8);
        buf.writeVarInt(profile.trackingChunkRadius);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        var length = buf.readVarInt();
        profile = new Profile(UUID.fromString(buf.readCharSequence(length, StandardCharsets.UTF_8).toString()),buf.readVarInt());
    }
}
