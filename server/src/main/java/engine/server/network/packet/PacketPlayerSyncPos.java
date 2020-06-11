package engine.server.network.packet;

import engine.server.network.PacketBuf;

public class PacketPlayerSyncPos implements Packet {

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

    private int flag;

    private int syncId;

    private PacketPlayerSyncPos(double posX, double posY, double posZ, float yaw, float pitch, int flag, int syncId) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flag = flag;
        this.syncId = syncId;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeVarInt(syncId);
        buf.writeVarInt(flag);
        if ((flag & X_MASK) != 0) {
            buf.writeDouble(posX);
        }
        if ((flag & Y_MASK) != 0) {
            buf.writeDouble(posY);
        }
        if ((flag & Z_MASK) != 0) {
            buf.writeDouble(posZ);
        }
        if ((flag & YAW_MASK) != 0) {
            buf.writeFloat(yaw);
        }
        if ((flag & PITCH_MASK) != 0) {
            buf.writeFloat(pitch);
        }
    }

    @Override
    public void read(PacketBuf buf) {
        syncId = buf.readVarInt();
        flag = buf.readVarInt();
        if ((flag & X_MASK) != 0) {
            posX = buf.readDouble();
        }
        if ((flag & Y_MASK) != 0) {
            posY = buf.readDouble();
        }
        if ((flag & Z_MASK) != 0) {
            posZ = buf.readDouble();
        }
        if ((flag & YAW_MASK) != 0) {
            yaw = buf.readFloat();
        }
        if ((flag & PITCH_MASK) != 0) {
            pitch = buf.readFloat();
        }
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

    public boolean hasXPosUpdated() {
        return (flag & X_MASK) != 0;
    }

    public boolean hasYPosUpdated() {
        return (flag & Y_MASK) != 0;
    }

    public boolean hasZPosUpdated() {
        return (flag & Z_MASK) != 0;
    }

    public boolean hasYawUpdated() {
        return (flag & YAW_MASK) != 0;
    }

    public boolean hasPitchUpdated() {
        return (flag & PITCH_MASK) != 0;
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

    public static class Builder {
        private double posX;
        private double posY;
        private double posZ;
        private float yaw;
        private float pitch;
        private int flag;
        private int syncId;

        public Builder(int syncId) {
            this.syncId = syncId;
        }

        public Builder x(double posX) {
            this.posX = posX;
            this.flag |= X_MASK;
            return this;
        }

        public Builder y(double posY) {
            this.posY = posY;
            this.flag |= Y_MASK;
            return this;
        }

        public Builder z(double posZ) {
            this.posZ = posZ;
            this.flag |= Z_MASK;
            return this;
        }

        public Builder yaw(float yaw) {
            this.yaw = yaw;
            this.flag |= YAW_MASK;
            return this;
        }

        public Builder pitch(float pitch) {
            this.pitch = pitch;
            this.flag |= PITCH_MASK;
            return this;
        }

        public PacketPlayerSyncPos build() {
            return new PacketPlayerSyncPos(posX, posY, posZ, yaw, pitch, flag, syncId);
        }
    }
}
