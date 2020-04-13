package engine.server.network;

import engine.Platform;
import engine.server.network.packet.Packet;
import engine.server.network.packet.PacketProvider;
import engine.server.network.packet.UnrecognizedPacketException;
import engine.util.RuntimeEnvironment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() != 0){
            var wrapper = new PacketBuf(in);
            var id = wrapper.readVarInt();
            Packet packet = Platform.getEngine().getRegistryManager().getRegistry(PacketProvider.class).orElseThrow().getValue(id).create();
            if(packet == null){
                throw new UnrecognizedPacketException("Unknown packet id: " + id);
            }
            else{
                packet.read(wrapper);
                if(wrapper.readableBytes() > 0) {
                    if (Platform.getRuntimeEnvironment() != RuntimeEnvironment.DEPLOYMENT) {
                        //DEBUG: print bytes
                        var nowReaderIndex = wrapper.readerIndex();
                        wrapper.resetReaderIndex();
                        var tmp = new byte[wrapper.readableBytes()];
                        wrapper.readBytes(tmp);
                        wrapper.readerIndex(nowReaderIndex);
                        Platform.getLogger().debug(String.format("Dumping problematic Packet #%d (%s):\n", id, packet.getClass().getSimpleName()) + dumpBytes(tmp));
                    }
                    throw new IOException(String.format("Packet #%d (%s) left out %d bytes while reading buffer!", id, packet.getClass().getSimpleName(), wrapper.readableBytes()));
                } else {
                    out.add(packet);
                }
            }
        }
    }

    private String dumpBytes(byte[] bytes) {
        var builder = new StringBuilder();
        var trans = new StringBuilder();
        builder.append(" HexDump  00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 \n");
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                builder.append(String.format("%08x  ", i));
            }
            builder.append(String.format("%02X ", bytes[i]));
            char c = ((char) bytes[i]);
            trans.append(Character.isISOControl(c) ? '.' : c);
            if ((i + 1) % 16 == 0) {
                builder.append("   ");
                builder.append(trans.toString());
                builder.append("\n");
                trans.delete(0, trans.length());
            }
            if (i + 1 == bytes.length) {
                for (int j = i % 16; j < 16; j++) {
                    builder.append("   ");
                }
                builder.append(trans.toString());
                trans.delete(0, trans.length());
            }
        }
        return builder.toString();
    }
}
