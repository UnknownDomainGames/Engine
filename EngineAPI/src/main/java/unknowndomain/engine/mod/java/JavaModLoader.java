package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.mod.ModLoader;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(ModDescriptor descriptor) {
        Logger modLogger = LoggerFactory.getLogger(descriptor.getModId());

        ClassLoader classLoader = new ModClassLoader(modLogger, descriptor.getSource(), Thread.currentThread().getContextClassLoader());
        
        try {
            Object instance = Class.forName(descriptor.getMainClass(), true, classLoader).newInstance();
            return new JavaModContainer(descriptor, classLoader, modLogger, instance);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace(); // TODO: throw it.
        }
        return null;
    }
}
