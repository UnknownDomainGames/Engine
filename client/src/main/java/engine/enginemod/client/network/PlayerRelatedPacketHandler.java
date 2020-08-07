package engine.enginemod.client.network;

import engine.Platform;
import engine.client.game.GameClient;
import engine.client.player.ClientPlayerImpl;
import engine.event.Listener;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.c2s.PacketPlayerMove;
import engine.server.network.packet.s2c.PacketPlayerPosView;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PlayerRelatedPacketHandler {

    @Listener
    public static void onPlayerPosView(PacketReceivedEvent<PacketPlayerPosView> event) {
        if (Platform.getEngine().isPlaying()) {
            var game = Platform.getEngine().getCurrentClientGame();
            if (game instanceof GameClient) {
                var player = ((GameClient) game).getClientPlayer();
                if (player.getControlledEntity() != null) {
                    var motion = player.getControlledEntity().getMotion().get(new Vector3f());
                    var pos = player.getControlledEntity().getPosition().get(new Vector3d());
                    if (event.getPacket().isPosXRelative()) {
                        pos.add(event.getPacket().getPosX(), 0, 0);
                    } else {
                        motion.x = 0;
                        pos.x = event.getPacket().getPosX();
                    }
                    if (event.getPacket().isPosYRelative()) {
                        pos.add(0, event.getPacket().getPosY(), 0);
                    } else {
                        motion.y = 0;
                        pos.y = event.getPacket().getPosY();
                    }
                    if (event.getPacket().isPosZRelative()) {
                        pos.add(0, 0, event.getPacket().getPosZ());
                    } else {
                        motion.z = 0;
                        pos.z = event.getPacket().getPosZ();
                    }

                    player.getControlledEntity().getPosition().set(pos);
                    player.getControlledEntity().getMotion().set(motion);
                    ((ClientPlayerImpl) player).lastX = pos.x;
                    ((ClientPlayerImpl) player).lastY = pos.y;
                    ((ClientPlayerImpl) player).lastZ = pos.z;
                    var rotation = player.getControlledEntity().getRotation().get(new Vector3f());
                    if (event.getPacket().isYawRelative()) {
                        rotation.add(event.getPacket().getYaw(), 0, 0);
                    } else {
                        rotation.x = event.getPacket().getYaw();
                    }
                    if (event.getPacket().isPitchRelative()) {
                        rotation.add(0, event.getPacket().getPitch(), 0);
                    } else {
                        rotation.y = event.getPacket().getPitch();
                    }
                    player.getControlledEntity().getRotation().set(rotation);
                    ((ClientPlayerImpl) player).lastYaw = rotation.x;
                    ((ClientPlayerImpl) player).lastPitch = rotation.y;
                    event.getHandler().sendPacket(event.getPacket().reply());
                    event.getHandler().sendPacket(PacketPlayerMove.update(pos, pos, rotation, false));
                }
            }
        }
    }
}
