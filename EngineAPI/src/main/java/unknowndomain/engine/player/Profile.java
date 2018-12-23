package unknowndomain.engine.player;

import java.util.UUID;

public class Profile {

    public final UUID uuid;
    public final int trackingChunkRadius;

    public Profile(UUID uuid, int trackingChunkRadius) {
        this.uuid = uuid;
        this.trackingChunkRadius = trackingChunkRadius;
    }
}
