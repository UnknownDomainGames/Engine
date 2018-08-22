package unknowndomain.engine.mod;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModClassLoader extends URLClassLoader {

    private static final String JAVA_PACKAGE_PREFIX = "java.";

    private final ModContainer mod;
    private final List<ClassLoader> dependencyClassLoaders = new ArrayList<>();

    public ModClassLoader(ModContainer mod, ClassLoader parent) {
        super(new URL[0], parent);
        this.mod = mod;
        addPath(mod.getSource());
    }

    public ModContainer getMod() {
        return mod;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addPath(Path path) {
        try {
            addURL(path.toUri().toURL());
        } catch (MalformedURLException e) {
            mod.getLogger().error(e.getMessage(), e);
        }
    }

    public List<ClassLoader> getDependencyClassLoaders() {
        return dependencyClassLoaders;
    }

    @Override
    public URL getResource(String name) {
        URL resource = findResource(name);
        if (resource != null)
            return resource;

        resource = findResourceFromDependencies(name);
        if (resource != null)
            return resource;

        return super.getResource(name);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name.startsWith(JAVA_PACKAGE_PREFIX)) {
                return findSystemClass(name);
            }

            Class<?> loadedClass;

            loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {

            }

            try {
                return findClass(name);
            } catch (ClassNotFoundException e) {

            }

            loadedClass = loadClassFromDependencies(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            throw new ClassNotFoundException(name);
        }
    }

    private Class<?> loadClassFromDependencies(String name) {
        for (ClassLoader classLoader : dependencyClassLoaders) {
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Try load class from next dependency.
            }
        }
        return null;
    }

    private URL findResourceFromDependencies(String name) {
        URL resource;
        for (ClassLoader classLoader : dependencyClassLoaders) {
            resource = classLoader.getResource(name);
            if (resource != null)
                return resource;
        }
        return null;
    }
}
