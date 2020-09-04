package engine.server.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import engine.Platform;
import engine.event.EventBus;
import engine.event.SimpleEventBus;
import engine.event.asm.AsmEventListenerFactory;
import engine.server.event.NetworkingStartEvent;
import engine.server.network.packet.Packet;
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

public class NetworkServer implements NetworkEndpoint {
    private EventBus eventBus;

    private List<ChannelFuture> channels = Collections.synchronizedList(new ArrayList<>());
    //NetworkHandler will handle their own client only. Therefore we want a list of them instead of only one instance
    private List<NetworkHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    public static final LazyObject<NioEventLoopGroup> DEFAULT_SERVER_ACCEPTOR_POOL = new LazyObject<>(() -> new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("Netty Server Acceptor #%d").setDaemon(true).build()));
    public static final LazyObject<NioEventLoopGroup> DEFAULT_SERVER_WORKER_POOL = new LazyObject<>(() -> new NioEventLoopGroup(new ThreadFactoryBuilder().setNameFormat("Netty Server Handler #%d").setDaemon(true).build()));

    public void run(int port) {
        run(null, port);
    }

    public void run(@Nullable InetAddress address, int port) {
        Platform.getLogger().debug(String.format("Start launching netty server at %s:%d", address == null ? "*" : address.getHostAddress(), port));
        var bossGroup = DEFAULT_SERVER_ACCEPTOR_POOL.get();
        var workerGroup = DEFAULT_SERVER_WORKER_POOL.get();
        prepareNetworkEventBus();
        synchronized (channels) {
            channels.add(new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            try {
                                ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                            } catch (ChannelException var3) {

                            }
                            ch.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new PacketStreamSplitter()).addLast("decoder", new PacketDecoder())
                                    .addLast("size_prepender", new PacketSizePrepender()).addLast("encoder", new PacketEncoder());
                            var handler = new NetworkHandler(Side.SERVER, eventBus);
                            ((HandshakeNetworkHandlerContext) handler.getContext()).setEndpoint(NetworkServer.this);
                            handlers.add(handler);
                            ch.pipeline().addLast("handler", handler);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).bind(port).syncUninterruptibly());
            Platform.getLogger().debug(String.format("Launched netty server at %s:%d", address == null ? "*" : address.getHostAddress(), port));
        }
    }

    public SocketAddress runLocal() {
        Platform.getLogger().debug("Start launching netty local server");
        var bossGroup = DEFAULT_SERVER_ACCEPTOR_POOL.get();
        var workerGroup = DEFAULT_SERVER_WORKER_POOL.get();
        prepareNetworkEventBus();
        ChannelFuture future;
        synchronized (channels) {
            future = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(LocalServerChannel.class)
                    .childHandler(new ChannelInitializer<>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            var handler = new NetworkHandler(Side.SERVER, eventBus);
                            ((HandshakeNetworkHandlerContext) handler.getContext()).setEndpoint(NetworkServer.this);
                            handlers.add(handler);
                            ch.pipeline().addLast("handler", handler);
                        }
                    }).bind(LocalAddress.ANY).syncUninterruptibly();
            Platform.getLogger().debug("Launched netty local server at %s:%d");
        }
        return future.channel().localAddress();
    }

    public void prepareNetworkEventBus() {
        if (eventBus == null) {
            eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
            Platform.getEngine().getEventBus().post(new NetworkingStartEvent(eventBus));
        }
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

    public void close() {
        for (Iterator<ChannelFuture> iterator = channels.iterator(); iterator.hasNext(); ) {
            ChannelFuture channel = iterator.next();
            try {
                channel.channel().close().sync();
            } catch (InterruptedException e) {
                Platform.getLogger().error("Interrupted whilst closing network channel", e);
            }
        }
    }

    public void sendToAll(Packet packet) {
        handlers.forEach(networkHandler -> networkHandler.sendPacket(packet));
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
