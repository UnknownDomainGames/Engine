package engine.server.network.packet.s2c;

import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;

public class PacketPlayerPosView implements Packet {

    private final static int X_MASK = 0b1;
    private final static int Y_MASK = 0b10;
    private final static int Z_MASK = 0b100;
    private final static int YAW_MASK = 0b1000;
    private final static int PITCH_MASK = 0b10000;

    private double posX;
    private double posY;
    private double posZ;

    private float yaw;
    private float pitch;

    private int relativeFlag;

    private int syncId;

    public PacketPlayerPosView() {

    }

    public PacketPlayerPosView(double posX, double posY, double posZ, float yaw, float pitch, int relativeFlag, int syncId) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.relativeFlag = relativeFlag;
        this.syncId = syncId;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeVarInt(syncId);
        buf.writeVarInt(relativeFlag);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
    }

    @Override
    public void read(PacketBuf buf) {
        syncId = buf.readVarInt();
        relativeFlag = buf.readVarInt();
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public int getSyncId() {
        return syncId;
    }

    public boolean isPosXRelative() {
        return (relativeFlag & X_MASK) != 0;
    }

    public boolean isPosYRelative() {
        return (relativeFlag & Y_MASK) != 0;
    }

    public boolean isPosZRelative() {
        return (relativeFlag & Z_MASK) != 0;
    }

    public boolean isYawRelative() {
        return (relativeFlag & YAW_MASK) != 0;
    }

    public boolean isPitchRelative() {
        return (relativeFlag & PITCH_MASK) != 0;
    }

    public Confirmed reply() {
        return new Confirmed(syncId);
    }

    public static class Confirmed implements Packet {

        private int syncId;

        public Confirmed() {

        }

        public Confirmed(int syncId) {
            this.syncId = syncId;
        }

        @Override
        public void write(PacketBuf buf) {
            buf.writeVarInt(syncId);
        }

        @Override
        public void read(PacketBuf buf) {
            syncId = buf.readVarInt();
        }

        public int getSyncId() {
            return syncId;
        }
    }

}
