package nullengine.mod.java;

import nullengine.mod.ModContainer;
import nullengine.mod.ModLoader;
import nullengine.mod.ModMetadata;
import nullengine.mod.exception.ModLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(Collection<Path> sources, ModMetadata metadata) {
        Logger modLogger = LoggerFactory.getLogger(metadata.getId());

        ModClassLoader classLoader = new ModClassLoader(modLogger, Thread.currentThread().getContextClassLoader());
        classLoader.addPaths(sources);

        try {
            Object instance = Class.forName(metadata.getMainClass(), true, classLoader).getDeclaredConstructor().newInstance();
            JavaModAssets assets = new JavaModAssets(sources, classLoader);
            JavaModContainer modContainer = new JavaModContainer(sources, classLoader, metadata, assets, modLogger, instance);
            classLoader.setMod(modContainer);
            assets.setMod(modContainer);
            return modContainer;
        } catch (ReflectiveOperationException | IOException e) {
            throw new ModLoadException(metadata.getId(), e);
        }
    }
}
