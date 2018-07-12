package unknowndomain.engine.mod;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ModClassLoader extends URLClassLoader {
	
	private final List<ClassLoader> dependencyClassLoaders = new ArrayList<>();

	public ModClassLoader(URL[] urls) {
		super(urls);
	}

	public List<ClassLoader> getDependencyClassLoaders() {
		return dependencyClassLoaders;
	}
}
