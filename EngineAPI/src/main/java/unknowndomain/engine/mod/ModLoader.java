package unknowndomain.engine.mod;

import java.io.IOException;
import java.io.InputStream;

public interface ModLoader {
    //    Collection<ModMetadata> seekIndeices();
//
//    Collection<ModMetadata> getLocalIndices();

//    ModContainer find(ModIdentifier identifier);

    ModContainer loadMod(String modId);

    ModContainer getModContainer(String modId);

    boolean hasMod(String modId);

    interface Source {
        boolean has(ModIdentifier identifier) throws IOException;

        InputStream open(ModIdentifier identifier) throws IOException;
    }
}
