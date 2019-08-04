package nullengine.mod.java;

import nullengine.mod.ModContainer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class ModClassLoader extends URLClassLoader {

    private final Logger logger;
    private final List<ClassLoader> dependentClassLoaders = new ArrayList<>();
    private final List<ClassLoader> isDependedBy = new ArrayList<>();

    private final LinkedList<ModTransfromer> transformers = new LinkedList<>();
    private final Set<String> unfoundClasses = new HashSet<>();

    private ModContainer mod;

    public static void addDependency(ModClassLoader loader, ModClassLoader dependency) {
        loader.dependentClassLoaders.add(dependency);
        dependency.isDependedBy.add(loader);
    }

    public ModClassLoader(String name, Logger logger, ClassLoader parent) {
        super(name, new URL[0], parent);
        this.logger = logger;
    }

    public void setMod(ModContainer owner) {
        if (this.mod != null) {
            throw new IllegalStateException("Mod container has been set.");
        }
        this.mod = owner;
    }

    public ModContainer getMod() {
        return mod;
    }

    public LinkedList<ModTransfromer> getTransformers() {
        return transformers;
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

    public List<ClassLoader> getDependentClassLoaders() {
        return dependentClassLoaders;
    }

    public List<ClassLoader> getIsDependedBy() {
        return isDependedBy;
    }

    public void addDependencies(Collection<ModContainer> mods) {
        for (ModContainer mod : mods) {
            ClassLoader classLoader = mod.getClassLoader();
            if (classLoader instanceof ModClassLoader) {
                addDependency(this, (ModClassLoader) classLoader);
            }
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                ClassLoader parent = getParent();
                return parent != null ? parent.loadClass(name) : findSystemClass(name);
            } catch (ClassNotFoundException ignored) {
            }

            loadedClass = loadClassFromDependencies(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                return findClass(name);
            } catch (ClassNotFoundException ignored) {
            }

            throw new ClassNotFoundException(name);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (unfoundClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }

        Class<?> result;

        String path = name.replace('.', '/').concat(".class");
        URL resource = getResource(path);
        if (resource == null) {
            unfoundClasses.add(name);
            throw new ClassNotFoundException(name);
        }

        try (InputStream inputStream = resource.openStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            for (ModTransfromer transformer : transformers) {
                bytes = transformer.transform(mod, name, bytes);
            }
            result = defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            unfoundClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }

        if (result == null) {
            unfoundClasses.add(name);
            throw new ClassNotFoundException(name);
        }
        return result;
    }

    private Class<?> loadClassFromDependencies(String name) {
        for (ClassLoader classLoader : dependentClassLoaders) {
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Try loadDirect class from next dependency.
            }
        }
        return null;
    }
}
