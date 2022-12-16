package engine.server.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import engine.Platform;
import engine.event.EventBus;
import engine.event.SimpleEventBus;
import engine.server.event.NetworkingStartEvent;
import engine.server.network.packet.Packet;
import engine.util.LazyObject;
import engine.util.Side;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NetworkClient implements NetworkEndpoint {
    private ChannelFuture future;
    private EventBus eventBus;

    private NetworkHandler handler;
    private NioEventLoopGroup workerGroup;
    public static final LazyObject<NioEventLoopGroup> DEFAULT_CLIENT_WORKER_POOL = new LazyObject<>(() -> new NioEventLoopGroup(new ThreadFactoryBuilder().setNameFormat("Netty Client Handler #%d").setDaemon(true).build()));
    public static final LazyObject<DefaultEventLoopGroup> LOCAL_CLIENT_WORKER_POOL = new LazyObject<>(() -> new DefaultEventLoopGroup(new ThreadFactoryBuilder().setNameFormat("Netty Client Handler #%d").setDaemon(true).build()));

    @Override
    public void run(InetAddress address, int port) {
        workerGroup = DEFAULT_CLIENT_WORKER_POOL.get();
        eventBus = new SimpleEventBus();
        Platform.getEngine().getEventBus().post(new NetworkingStartEvent(eventBus));
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
                        ch.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                                .addLast("splitter", new PacketStreamSplitter()).addLast("decoder", new PacketDecoder())
                                .addLast("size_prepender", new PacketSizePrepender()).addLast("encoder", new PacketEncoder());
                        handler = new NetworkHandler(Side.CLIENT, eventBus);
                        ((HandshakeNetworkHandlerContext) handler.getContext()).setEndpoint(NetworkClient.this);
                        ch.pipeline().addLast("handler", handler);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true).connect(address, port).syncUninterruptibly();
        future = channelFuture;
    }

    public void runLocal(SocketAddress localServerAddress) {
        var workerGroup = LOCAL_CLIENT_WORKER_POOL.get();
        eventBus = new SimpleEventBus();
        Platform.getEngine().getEventBus().post(new NetworkingStartEvent(eventBus));
        var channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(LocalChannel.class)
                .handler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        handler = new NetworkHandler(Side.CLIENT, eventBus);
                        ((HandshakeNetworkHandlerContext) handler.getContext()).setEndpoint(NetworkClient.this);
                        ch.pipeline().addLast("handler", handler);
                    }
                })
                .connect(localServerAddress).syncUninterruptibly();
        future = channelFuture;
    }

    public void send(Packet msg) {
        future.channel().writeAndFlush(msg);
    }

    public NetworkHandler getHandler() {
        return handler;
    }

    @Override
    public void tick() {
        handler.tick();
    }

    @Override
    public void close() {
//        if (handler != null && handler.isChannelOpen()) {
//            handler.closeChannel();
//        }
        if (future != null && future.channel().isOpen()) {
            future.channel().close().syncUninterruptibly();
        }
    }
}
