package engine.server.network.packet.s2c;

import engine.block.Block;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;
import engine.world.World;

import java.io.IOException;

public class PacketBlockUpdate implements Packet {

    private String worldName;
    private BlockPos pos;
    private Block block;

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
        buf.writeVarInt(Registries.getBlockRegistry().getId(block));
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        worldName = buf.readString();
        pos = buf.readBlockPos();
        block = Registries.getBlockRegistry().getValue(buf.readVarInt());
    }

    public String getWorldName() {
        return worldName;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }
}
