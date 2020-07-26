package engine.server.network.packet;

import engine.server.network.PacketBuf;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

/**
 * This C2S packet is used for handling player movement
 * for S2C, use <code>PacketPlayerSyncPos</code>
 */
public class PacketPlayerMove implements Packet {

    private final static int POSITION_CHANGE_MASK = 0b1;
    private final static int LOOK_CHANGE_MASK = 0b10;

    private double posX;
    private double posY;
    private double posZ;

    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;

    private float yaw;
    private float pitch;

    private boolean onGround;

    private int flag;

    public PacketPlayerMove() {

    }

    public PacketPlayerMove(Vector3dc pos, boolean onGround) {
        this.posX = pos.x();
        this.posY = pos.y();
        this.posZ = pos.z();
        this.onGround = onGround;
        flag = POSITION_CHANGE_MASK;
    }

    public static PacketPlayerMove updatePosition(Vector3dc pos, Vector3dc lastPos, boolean onGround) {
        var p = new PacketPlayerMove();
        p.posX = pos.x();
        p.posY = pos.y();
        p.posZ = pos.z();
        p.lastPosX = lastPos.x();
        p.lastPosY = lastPos.y();
        p.lastPosZ = lastPos.z();
        p.onGround = onGround;
        p.flag = POSITION_CHANGE_MASK;
        return p;
    }

    public static PacketPlayerMove updateLookAt(Vector3fc lookat, boolean onGround) {
        var p = new PacketPlayerMove();
        p.yaw = lookat.x();
        p.pitch = lookat.y();
        p.onGround = onGround;
        p.flag = LOOK_CHANGE_MASK;
        return p;
    }

    public static PacketPlayerMove update(Vector3dc pos, Vector3dc lastPos, Vector3fc lookat, boolean onGround) {
        var p = new PacketPlayerMove();
        p.posX = pos.x();
        p.posY = pos.y();
        p.posZ = pos.z();
        p.lastPosX = lastPos.x();
        p.lastPosY = lastPos.y();
        p.lastPosZ = lastPos.z();
        p.yaw = lookat.x();
        p.pitch = lookat.y();
        p.onGround = onGround;
        p.flag = POSITION_CHANGE_MASK | LOOK_CHANGE_MASK;
        return p;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeVarInt(flag);
        buf.writeBoolean(onGround);
        if ((flag & POSITION_CHANGE_MASK) != 0) {
            buf.writeDouble(posX);
            buf.writeDouble(posY);
            buf.writeDouble(posZ);
            buf.writeDouble(lastPosX);
            buf.writeDouble(lastPosY);
            buf.writeDouble(lastPosZ);
        }
        if ((flag & LOOK_CHANGE_MASK) != 0) {
            buf.writeFloat(yaw);
            buf.writeFloat(pitch);
        }
    }

    @Override
    public void read(PacketBuf buf) {
        flag = buf.readVarInt();
        onGround = buf.readBoolean();
        if ((flag & POSITION_CHANGE_MASK) != 0) {
            posX = buf.readDouble();
            posY = buf.readDouble();
            posZ = buf.readDouble();
            lastPosX = buf.readDouble();
            lastPosY = buf.readDouble();
            lastPosZ = buf.readDouble();
        }
        if ((flag & LOOK_CHANGE_MASK) != 0) {
            yaw = buf.readFloat();
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

    public double getLastPosX() {
        return lastPosX;
    }

    public double getLastPosY() {
        return lastPosY;
    }

    public double getLastPosZ() {
        return lastPosZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean hasPositionUpdated() {
        return (flag & POSITION_CHANGE_MASK) != 0;
    }

    public boolean hasLookUpdated() {
        return (flag & LOOK_CHANGE_MASK) != 0;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
