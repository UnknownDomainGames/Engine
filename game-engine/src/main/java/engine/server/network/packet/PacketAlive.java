package engine.server.network.packet;

import engine.server.network.PacketBuf;

import java.io.IOException;

public class PacketAlive implements Packet {

    private boolean pong;

    public PacketAlive() {

    }

    public PacketAlive(boolean pong) {
        this.pong = pong;
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
//        buf.writeBoolean(pong);
        buf.writeVarInt(pong ? 1 : 0); //TODO somehow cannot write boolean only
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        pong = buf.readVarInt() == 1;
    }

    public boolean isPong() {
        return pong;
    }
}
