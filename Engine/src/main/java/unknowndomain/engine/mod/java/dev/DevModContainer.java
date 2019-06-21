package unknowndomain.engine.mod.java.dev;

import org.slf4j.Logger;
import unknowndomain.engine.mod.ModAssets;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.java.JavaModContainer;

public class DevModContainer extends JavaModContainer {
    public DevModContainer(ModMetadata descriptor, ClassLoader classLoader, ModAssets assets, Logger logger, Object instance) {
        super(descriptor, classLoader, assets, logger, instance);
    }
}
