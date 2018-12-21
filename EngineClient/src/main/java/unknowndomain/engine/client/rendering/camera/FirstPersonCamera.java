package unknowndomain.engine.client.rendering.camera;

import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.player.Player;

public class FirstPersonCamera implements Camera {

    private final Player player;

    public FirstPersonCamera(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Vector3f getPosition(float parTick) {
        Vector3d position = getPlayer().getMountingEntity().getPosition();
        Vector3f motion = getPlayer().getMountingEntity().getMotion();
        return new Vector3f((float) position.x + motion.x * parTick, (float) position.y + motion.y * parTick, (float) position.z + motion.z * parTick);
    }

    @Override
    public Vector3f getLookAt(float parTick) {
        Vector3f rotation = getPlayer().getMountingEntity().getRotation();
        Vector3f front = new Vector3f((float) (Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))),
                (float) Math.sin(Math.toRadians(rotation.y)), (float) (Math.cos(Math.toRadians(rotation.y)) * Math.sin(Math.toRadians(rotation.x)))).normalize();
        return getPosition(parTick).add(front, new Vector3f());
    }
}
