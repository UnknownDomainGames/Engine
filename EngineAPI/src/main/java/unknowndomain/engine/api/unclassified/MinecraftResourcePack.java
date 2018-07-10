package unknowndomain.engine.api.unclassified;

import java.io.IOException;

public interface MinecraftResourcePack extends ResourceSource {
    String[] domains() throws IOException;
}
