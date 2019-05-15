package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.mod.ModLoader;
import unknowndomain.engine.mod.exception.ModLoadException;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(ModDescriptor descriptor) {
        Logger modLogger = LoggerFactory.getLogger(descriptor.getModId());

        ModClassLoader classLoader = new ModClassLoader(modLogger, descriptor.getSource(), Thread.currentThread().getContextClassLoader());

        try {
            Object instance = Class.forName(descriptor.getMainClass(), true, classLoader).newInstance();
            JavaModContainer modContainer = new JavaModContainer(descriptor, classLoader, modLogger, instance);
            classLoader.setMod(modContainer);
            return modContainer;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ModLoadException(descriptor.getModId(), e);
        }
    }
}
