package engine.server.network;

import engine.Platform;
import engine.event.EventBus;
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

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;
    //which is THIS handler located
    private final Side instanceSide;
    private ConnectionStatus status;

    private final EventBus eventBus;

    public NetworkHandler(Side side) {
        this(side, null);
    }

    public NetworkHandler(Side side, EventBus bus) {
        instanceSide = side;
        status = ConnectionStatus.HANDSHAKE;
        this.eventBus = bus != null ? bus : Platform.getEngine().getEventBus();
    }

    public Side getSide() {
        return instanceSide;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
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

    public void closeChannel(String reason){
        if(this.channel != null && this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            var event = new NetworkDisconnectedEvent(reason);
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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        packetInCounter++;
        if (packet instanceof PacketAlive && !((PacketAlive) packet).isPong()) {
            sendPacket(new PacketAlive(true));
        }
        eventBus.post(new PacketReceivedEvent(this, packet));
    }

    private boolean exceptionMet = false;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (channel.isOpen()) {
            if (cause instanceof TimeoutException) {
                closeChannel("Connection timed out");
            } else {
                if (!exceptionMet) {
                    exceptionMet = true;
                    Platform.getLogger().warn("exception thrown in connection", cause);

                    sendPacket(new PacketDisconnect(cause.getMessage()), future -> closeChannel(cause.getMessage()));
                    setAutoRead(false);
                } else {
                    Platform.getLogger().warn("DOUBLE FAILURE! exception thrown in connection", cause);
                    closeChannel(cause.getMessage());
                }
            }
        }
    }

    public void setAutoRead(boolean flag) {
        channel.config().setAutoRead(flag);
    }

    public boolean isChannelOpen() {
        return channel != null && channel.isOpen();
    }

    // This method will not send packet immediately
    public void pendPacket(Packet packet) {
        pendPacket(packet, null);
    }

    public void pendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        if (channel != null) {
            packetOutCounter++;
            var channelFuture = channel.write(packet);
            if (future != null) {
                channelFuture.addListener(future);
            }
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public void sendPendingPackets() {
        if (channel != null) {
            channel.flush();
        }
    }

    public void sendPacket(Packet packet) {
        pendPacket(packet);
        sendPendingPackets();
    }

    public void sendPacket(Packet packet, @Nullable GenericFutureListener<Future<? super Void>> future) {
        pendPacket(packet, future);
        sendPendingPackets();
    }

    private int packetOutCounter;
    private int packetInCounter;
    private int tick;
    private float packetOutAverage;
    private float packetInAverage;

    public void tick() {
        if (tick++ % 20 == 0) {
            this.packetOutAverage = packetOutAverage * 0.75f + packetOutCounter * 0.25f;
            this.packetInAverage = packetInAverage * 0.75f + packetInCounter * 0.25f;
            packetOutCounter = 0;
            packetInCounter = 0;
            if (packetOutAverage == 0) {
                sendPacket(new PacketAlive(false));
            }
        }
    }

}
