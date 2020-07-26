package engine.server.network;

import engine.Platform;
import engine.event.EventBus;
import engine.event.EventException;
import engine.server.event.NetworkDisconnectedEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.Packet;
import engine.server.network.packet.PacketAlive;
import engine.server.network.packet.PacketDisconnect;
import engine.util.Side;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;
    //which is THIS handler located
    private final Side instanceSide;
    private ConnectionStatus status;
    private NetworkHandlerContext context;
    private final EventBus eventBus;

    public NetworkHandler(Side side) {
        this(side, null);
    }

    public NetworkHandler(Side side, EventBus bus) {
        instanceSide = side;
        status = ConnectionStatus.HANDSHAKE;
        context = new HandshakeNetworkHandlerContext();
        this.eventBus = bus != null ? bus : Platform.getEngine().getEventBus();
    }

    public Side getSide() {
        return instanceSide;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return channel.localAddress();
    }

    public void setStatus(ConnectionStatus status, NetworkHandlerContext context) {
        if (context.isSideDepends() && context.getContextSide() != instanceSide) {
            throw new IllegalArgumentException("mismatched network side!");
        }
        if (context.getConnectionStatus() != status) {
            throw new IllegalArgumentException("mismatched connection status");
        }
        this.status = status;
        this.context = context;
    }

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

    public void closeChannel() {
        closeChannel("");
    }

    private String disconnectionReason;

    public void closeChannel(String reason) {
        if (this.channel != null && this.channel.isOpen()) {
            disconnectionReason = reason;
            this.channel.config().setAutoRead(false);
            this.channel.close().awaitUninterruptibly();
            postDisconnect();
        }
    }

    private boolean disconnected;

    public void postDisconnect() {
        if (!disconnected) {
            disconnected = true;
            var event = new NetworkDisconnectedEvent(this, disconnectionReason);
            var mainBus = Platform.getEngine().getEventBus();
            if (eventBus != mainBus) {
                eventBus.post(event);
            }
            mainBus.post(event);
        }
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public NetworkHandlerContext getContext() {
        return context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        packetInCounter++;
        if (packet instanceof PacketAlive) {
            Platform.getLogger().debug("Alive Packet received {}", getSide());
            if (!((PacketAlive) packet).isPong()) {
                sendPacket(new PacketAlive(true));
            }
        }
        eventBus.post(new PacketReceivedEvent<>(this, packet));
    }

    private boolean exceptionMet = false;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        var ex = cause;
        if (channel.isOpen()) {
            if (cause instanceof TimeoutException) {
                closeChannel("Connection timed out");
            } else {
                if (!exceptionMet) {
                    exceptionMet = true;
                    Platform.getLogger().warn("exception thrown in connection", cause);
                    if (cause instanceof EventException) {
                        ex = cause.getCause();
                    }
                    Throwable finalEx = ex;
                    sendPacket(new PacketDisconnect(ex.getMessage()), future -> closeChannel(finalEx.getMessage()));
                    setAutoRead(false);
                } else {
                    Platform.getLogger().warn("DOUBLE FAILURE! exception thrown in connection", cause);
                    closeChannel(cause.getMessage());
                }
            }
        } else {
            closeChannel(cause.getMessage());
        }
    }

    public void setAutoRead(boolean flag) {
        channel.config().setAutoRead(flag);
    }

    public boolean isChannelOpen() {
        return channel != null && channel.isOpen();
    }

    private List<PendingPacket> pendingPackets = new ArrayList<>();

    // This method will not send packet immediately
    public void pendPacket(Packet packet) {
        pendPacket(packet, null);
    }

    public void pendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        pendingPackets.add(new PendingPacket(packet, future));
    }

    public void sendPendingPackets() {
        for (Iterator<PendingPacket> iterator = pendingPackets.iterator(); iterator.hasNext(); ) {
            PendingPacket pendingPacket = iterator.next();
            sendPacketInternal(pendingPacket.getPacket(), pendingPacket.getOnComplete());
            iterator.remove();
        }
    }

    public void sendPacket(Packet packet) {
        sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        sendPendingPackets();
        sendPacketInternal(packet, future);
    }

    private void sendPacketInternal(Packet packet, GenericFutureListener<Future<? super Void>> future) {
        if (channel != null) {
            packetOutCounter++;
            var channelFuture = channel.writeAndFlush(packet);
            if (future != null) {
                channelFuture.addListener(future);
            }
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    private int packetOutCounter;
    private int packetInCounter;
    private int tick;
    private float packetOutAverage;
    private float packetInAverage;
    private static final int RANGE_SECONDS_COUNTED_FOR_AVERAGE = 5;

    public void tick() {
        if (tick++ % 20 == 0) {
            // The algorithm of calculating average is as follows:
            // 1. multiply the old average by RANGE_SECONDS_COUNTED_FOR_AVERAGE to become total packet in/out in previous 5 seconds
            // 2. throw away 1-sec component in the total packet in/out
            // 3. add up counter into it
            // 4. divide that by RANGE_SECONDS_COUNTED_FOR_AVERAGE
            this.packetOutAverage = (packetOutAverage * (RANGE_SECONDS_COUNTED_FOR_AVERAGE - 1) + packetOutCounter) / (float) RANGE_SECONDS_COUNTED_FOR_AVERAGE;
            this.packetInAverage = (packetInAverage * (RANGE_SECONDS_COUNTED_FOR_AVERAGE - 1) + packetInCounter) / (float) RANGE_SECONDS_COUNTED_FOR_AVERAGE;
            packetOutCounter = 0;
            packetInCounter = 0;
            if (Math.round(packetOutAverage * RANGE_SECONDS_COUNTED_FOR_AVERAGE) == 0) {
                sendPacket(new PacketAlive(false));
            }
        }
    }

    private static class PendingPacket {
        private Packet packet;
        private GenericFutureListener<Future<? super Void>> onComplete;

        private PendingPacket(Packet packet, GenericFutureListener<Future<? super Void>> onComplete) {
            this.packet = packet;
            this.onComplete = onComplete;
        }

        public Packet getPacket() {
            return packet;
        }

        public GenericFutureListener<Future<? super Void>> getOnComplete() {
            return onComplete;
        }
    }

}
