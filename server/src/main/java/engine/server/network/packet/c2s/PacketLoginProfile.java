package engine.server.network.packet.c2s;

import engine.player.Profile;
import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;

import java.io.IOException;
import java.util.UUID;

public class PacketLoginProfile implements Packet {

    private Profile profile;

    public PacketLoginProfile() {

    }

    public PacketLoginProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        var uuid = profile.getUuid();
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeString(profile.getName());
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        profile = new Profile(new UUID(buf.readLong(), buf.readLong()), buf.readString());
    }

    public Profile getProfile() {
        return profile;
    }
}
