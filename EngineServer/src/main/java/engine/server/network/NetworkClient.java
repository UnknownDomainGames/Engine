package engine.server.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import engine.server.network.packet.Packet;
import engine.util.Side;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NetworkClient {
    private ChannelFuture future;
    private NetworkHandler handler;
    private NioEventLoopGroup workerGroup;
    public void run(InetAddress address, int port){
        workerGroup = new NioEventLoopGroup();
        var channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        try {
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException var3) {

                        }
                        ch.pipeline().addLast("timeout", new ReadTimeoutHandler(10)).addLast("decoder", new PacketDecoder())
                                .addLast("encoder", new PacketEncoder());
                        handler = new NetworkHandler(Side.CLIENT);
                        ch.pipeline().addLast("handler", handler);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true).connect(address, port).syncUninterruptibly();
        future = channelFuture;
    }

    public void runLocal(SocketAddress localServerAddress){
        var workerGroup = new NioEventLoopGroup();
        var channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(LocalChannel.class)
                .handler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        handler = new NetworkHandler(Side.CLIENT);
                        ch.pipeline().addLast("handler", handler);
                    }
                })
                .connect(localServerAddress).syncUninterruptibly();
        future = channelFuture;
    }

    public void send(Packet msg){
        future.channel().writeAndFlush(msg);
    }

    public NetworkHandler getHandler() {
        return handler;
    }

    public void close(){
        if(handler.isChannelOpen()){
            handler.closeChannel();
        }
        workerGroup.shutdownGracefully();
    }
}
