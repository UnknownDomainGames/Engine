package nullengine.server.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NetworkClient {
    private ChannelFuture future;
    public void run(InetAddress address, int port){
        var workerGroup = new NioEventLoopGroup();
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
                        ch.pipeline().addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder());
                        ch.pipeline().addLast("handler", new NetworkClientHandler());
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
                        ch.pipeline().addLast("handler", new NetworkClientHandler());
                    }
                })
                .connect(localServerAddress).syncUninterruptibly();
        future = channelFuture;
    }

    public void send(String msg){
        future.channel().writeAndFlush(msg);
    }
}
