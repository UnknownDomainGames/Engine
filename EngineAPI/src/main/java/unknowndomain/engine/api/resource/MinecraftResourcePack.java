package unknowndomain.engine.api.resource;

import java.io.IOException;

public interface MinecraftResourcePack extends ResourceSource {
    String[] domains() throws IOException;
}
