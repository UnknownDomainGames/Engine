package unknowndomain.engine.mod.java;

import org.slf4j.Logger;
import unknowndomain.engine.mod.ModContainer;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModClassLoader extends URLClassLoader {
    private static final String JAVA_PACKAGE_PREFIX = "java.";

    private final Logger logger;
    private final List<ClassLoader> dependencyClassLoaders = new ArrayList<>();
    private final List<ClassLoader> isDependedBy = new ArrayList<>();

    private ModContainer owner;

    public ModClassLoader(Logger logger, ClassLoader parent) {
        super(new URL[0], parent);
        this.logger = logger;
    }

    public void setMod(ModContainer owner) {
        if (this.owner != null) {
            throw new IllegalStateException("Mod container has been set.");
        }
        this.owner = owner;
    }

    public ModContainer getMod() {
        return owner;
    }

    public static void addDependency(ModClassLoader loader, ModClassLoader dependency) {
        loader.dependencyClassLoaders.add(dependency);
        dependency.isDependedBy.add(loader);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addPath(Path path) {
        try {
            addURL(path.toUri().toURL());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void addPaths(Collection<Path> paths) {
        for (Path path : paths) {
            try {
                addURL(path.toUri().toURL());
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            }
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
                return findClass(name);
            } catch (ClassNotFoundException e) {

            }

            loadedClass = loadClassFromDependencies(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {

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
