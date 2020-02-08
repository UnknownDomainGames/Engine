package engine.player;

import java.util.UUID;

public class Profile {

    private final UUID uuid;
    private final String name;

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
