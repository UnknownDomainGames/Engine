package engine.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketStreamSplitter extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (true) {
            in.markReaderIndex();
            if (!in.isReadable()) {
                break;
            }
            var wrapper = new PacketBuf(in);
            var len = wrapper.readVarInt();
            if (in.isReadable(len)) {
                out.add(wrapper.readBytes(len));
            } else {
                in.resetReaderIndex();
                break;
            }
        }
    }
}
