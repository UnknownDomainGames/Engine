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
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(Collection<Path> sources, ModMetadata metadata, List<ModContainer> dependencies) {
        Logger logger = LoggerFactory.getLogger(isNullOrEmpty(metadata.getName()) ? metadata.getId() : metadata.getName());

        ModClassLoader classLoader = new ModClassLoader(logger, JavaModLoader.class.getClassLoader());
        classLoader.addPaths(sources);
        classLoader.addDependencies(dependencies);

        try {
            Object instance = Class.forName(metadata.getMainClass(), true, classLoader).getDeclaredConstructor().newInstance();
            JavaModAssets assets = new JavaModAssets(sources, classLoader);
            JavaModContainer modContainer = new JavaModContainer(sources, classLoader, metadata, assets, logger, instance);
            classLoader.setMod(modContainer);
            assets.setMod(modContainer);
            return modContainer;
        } catch (ReflectiveOperationException | IOException e) {
            throw new ModLoadException(metadata.getId(), e);
        }
    }
}