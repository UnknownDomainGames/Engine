package engine.server.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import engine.Platform;
import engine.event.EventBus;
import engine.event.SimpleEventBus;
import engine.event.asm.AsmEventListenerFactory;
import engine.server.event.NetworkingStartEvent;
import engine.util.LazyObject;
import engine.util.Side;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NetworkServer {
    private ChannelFuture future;

    private EventBus eventBus;

    //NetworkHandler will handle their own client only. Therefore we want a list of them instead of only one instance
    private List<NetworkHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    public static final LazyObject<NioEventLoopGroup> DEFAULT_SERVER_ACCEPTOR_POOL = new LazyObject<>(() -> new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("Netty Server Acceptor #%d").setDaemon(true).build()));
    public static final LazyObject<NioEventLoopGroup> DEFAULT_SERVER_WORKER_POOL = new LazyObject<>(() -> new NioEventLoopGroup(new ThreadFactoryBuilder().setNameFormat("Netty Server Handler #%d").setDaemon(true).build()));

    public void run(int port) {
        run(null, port);
    }

    public void run(@Nullable InetAddress address, int port) {
        Platform.getLogger().debug("Start launching server at {}:{}", address == null ? "*" : address.getHostAddress(), port);
        var bossGroup = DEFAULT_SERVER_ACCEPTOR_POOL.get();
        var workerGroup = DEFAULT_SERVER_WORKER_POOL.get();
        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
        Platform.getEngine().getEventBus().post(new NetworkingStartEvent(eventBus));
        future = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        try {
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {
                        }
                        var handler = new NetworkHandler(Side.SERVER, eventBus);
                        ch.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                                .addLast("splitter", new PacketStreamSplitter())
                                .addLast("decoder", new PacketDecoder())
                                .addLast("size_prepender", new PacketSizePrepender())
                                .addLast("encoder", new PacketEncoder())
                                .addLast("handler", handler);
                        handlers.add(handler);
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true).bind(port).syncUninterruptibly();
        Platform.getLogger().info("Launched server at {}:{}", address == null ? "*" : address.getHostAddress(), port);
    }

    public SocketAddress runLocal() {
        Platform.getLogger().debug("Start launching local server");
        var bossGroup = DEFAULT_SERVER_ACCEPTOR_POOL.get();
        var workerGroup = DEFAULT_SERVER_WORKER_POOL.get();
        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
        Platform.getEngine().getEventBus().post(new NetworkingStartEvent(eventBus));
        future = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(LocalServerChannel.class)
                .childHandler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        var handler = new NetworkHandler(Side.SERVER, eventBus);
                        handlers.add(handler);
                        ch.pipeline().addLast("handler", handler);
                    }
                }).bind(LocalAddress.ANY).syncUninterruptibly();
        Platform.getLogger().info("Launched local server");
        return future.channel().localAddress();
    }

    public void tick() {
        synchronized (handlers) {
            for (Iterator<NetworkHandler> iterator = handlers.iterator(); iterator.hasNext(); ) {
                NetworkHandler handler = iterator.next();
                if (handler.isChannelOpen()) {
                    handler.tick();
                } else {
                    handler.postDisconnect();
                    iterator.remove();
                }
            }
        }
    }

    public void shutdown() {
        future.channel().close().syncUninterruptibly();
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
