package nullengine.event.asm;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

class SafeClassDefiner {

    private final Map<ClassLoader, GeneratedClassLoader> loaders = Collections.synchronizedMap(new WeakHashMap<>());

    public Class<?> defineClass(ClassLoader parentLoader, String name, byte[] data) {
        GeneratedClassLoader loader = loaders.computeIfAbsent(parentLoader, GeneratedClassLoader::new);
        synchronized (loader.getClassLoadingLock(name)) {
            if (loader.hasClass(name)) {
                throw new IllegalStateException(name + " already defined");
            }
            Class<?> clazz = loader.define(name, data);
            assert clazz.getName().equals(name);
            return clazz;
        }
    }

    private static class GeneratedClassLoader extends ClassLoader {
        static {
            ClassLoader.registerAsParallelCapable();
        }

        protected GeneratedClassLoader(ClassLoader parent) {
            super(parent);
        }

        private Class<?> define(String name, byte[] data) {
            synchronized (getClassLoadingLock(name)) {
                assert !hasClass(name);
                Class<?> clazz = defineClass(name, data, 0, data.length);
                resolveClass(clazz);
                return clazz;
            }
        }

        @Override
        public Object getClassLoadingLock(String name) {
            return super.getClassLoadingLock(name);
        }

        public boolean hasClass(String name) {
            synchronized (getClassLoadingLock(name)) {
                try {
                    Class.forName(name);
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
    }
}
