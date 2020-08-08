package engine.server.network.packet.c2s;

import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;
import engine.world.hit.HitResult;

import java.io.IOException;

public class PacketPlayerAction implements Packet {

    private Action action;
    private HitResult hitResult;

    public PacketPlayerAction() {
    }

    public PacketPlayerAction(Action action, HitResult hitResult) {
        this.action = action;
        this.hitResult = hitResult;
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeEnum(action);
        buf.writeHitResult(hitResult);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        action = buf.readEnum(Action.class);
        hitResult = buf.readHitResult();
    }

    public Action getAction() {
        return action;
    }

    public HitResult getHitResult() {
        return hitResult;
    }

    public enum Action {
        START_BREAK_BLOCK,
        STOP_BREAK_BLOCK,
        INTERRUPTED,
        INTERACT_BLOCK
    }
}
