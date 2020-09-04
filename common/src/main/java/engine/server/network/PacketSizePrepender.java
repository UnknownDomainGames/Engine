package engine.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketSizePrepender extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        var length = msg.readableBytes();
        var wrapper = new PacketBuf(out);
        wrapper.ensureWritable(PacketBuf.getVarIntSize(length) + length);
        wrapper.writeVarInt(length);
        wrapper.writeBytes(msg, msg.readerIndex(), length);
    }
}
