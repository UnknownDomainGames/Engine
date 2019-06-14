package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.exception.ModLoadException;

import java.io.IOException;
import java.nio.file.FileSystems;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(ModDescriptor descriptor) {
        Logger modLogger = LoggerFactory.getLogger(descriptor.getModId());

        ModClassLoader classLoader = new ModClassLoader(modLogger, Thread.currentThread().getContextClassLoader());
        classLoader.addPath(descriptor.getSource());

        try {
            Object instance = Class.forName(descriptor.getMainClass(), true, classLoader).newInstance();
            JavaModAssets assets = new JavaModAssets(FileSystems.newFileSystem(descriptor.getSource(), classLoader));
            JavaModContainer modContainer = new JavaModContainer(descriptor, classLoader, assets, modLogger, instance);
            classLoader.setMod(modContainer);
            assets.setMod(modContainer);
            return modContainer;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
            throw new ModLoadException(descriptor.getModId(), e);
        }
    }
}
