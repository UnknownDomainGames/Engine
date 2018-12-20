package unknowndomain.engine.event.mod;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.mod.ModMetadata;

public class ModStartLoadEvent implements Event {
    private String modid;
    private ModMetadata metadata;

    public ModStartLoadEvent(String modid, ModMetadata metadata) {
        this.modid = modid;
        this.metadata = metadata;
    }

    public String getModid() {
        return modid;
    }

    public ModMetadata getMetadata() {
        return metadata;
    }
}
