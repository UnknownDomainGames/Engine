package nullengine.server.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.timeout.TimeoutException;
import nullengine.server.network.packet.Packet;

public class NetworkServerHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        closeChannel();
    }

    public void closeChannel(){
        if(this.channel.isOpen()){
            this.channel.close().awaitUninterruptibly();
        }
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof TimeoutException){
            closeChannel();
        }
        else{
            closeChannel();
        }
    }

    public boolean isChannelOpen(){
        return channel != null && channel.isOpen();
    }

}
