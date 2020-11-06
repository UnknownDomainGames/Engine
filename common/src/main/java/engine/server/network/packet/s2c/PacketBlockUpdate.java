package engine.server.network.packet.s2c;

import engine.block.state.BlockState;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;
import engine.world.World;

import java.io.IOException;

public class PacketBlockUpdate implements Packet {

    private String worldName;
    private BlockPos pos;
    private BlockState block;

    public PacketBlockUpdate() {
    }

    public PacketBlockUpdate(World world, BlockPos pos) {
        this.worldName = world.getName();
        this.pos = pos;
        this.block = world.getBlock(pos);
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(worldName);
        buf.writeBlockPos(pos);
        buf.writeVarInt(Registries.getBlockRegistry().getStateId(block));
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        worldName = buf.readString();
        pos = buf.readBlockPos();
        block = Registries.getBlockRegistry().getStateFromId(buf.readVarInt());
    }

    public String getWorldName() {
        return worldName;
    }

    public BlockState getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }
}
