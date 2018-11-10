package unknowndomain.engine.player;

import org.joml.AABBd;

import java.util.UUID;

public class Profile {

    public final UUID uuid;
    public final int trackingChunkRadius;
    private AABBd boundingBox = new AABBd(-0.4, -1.5, -0.4, 0.4, 0, 0.4);
    private String mountingEntity;

    public Profile(UUID uuid, int trackingChunkRadius) {
        this.uuid = uuid;
        this.trackingChunkRadius = trackingChunkRadius;
    }

    public AABBd getBoundingBox() {
        return boundingBox;
    }

    public String getMountingEntity() {
        return mountingEntity;
    }
}
