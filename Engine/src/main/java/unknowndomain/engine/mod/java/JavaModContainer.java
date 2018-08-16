package unknowndomain.engine.mod.java;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.mod.ModClassLoader;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModState;
//TODO: collect mod's class loader, instance of mod main class, mod config, mod looger, config dir.
public class JavaModContainer implements ModContainer {

	private final Path source;

    private final String modid;

    private final Logger logger;

	/**
	 * Class loader of the mod.
	 */
	private ModClassLoader classLoader;

    private Object instance;
    
    private ModMetadata metadata;

    private ModState state;

    public JavaModContainer(String modid, Path src){
        this.modid = modid;
        this.logger = LoggerFactory.getLogger(modid);
        source = src;
    }

    @Override
	public String getModId() {
		return modid;
	}

	@Override
	public Object getMainClassInstance() {
		return instance;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public Path getSource() {
		return source;
	}

	private boolean enabled;

	@Override
	public boolean isEnable() {
		return enabled;
	}

	@Override
	public void setEnable(boolean enable) {
		this.enabled = enable;
		
	}

	@Override
	public ModMetadata getMetadata() {
		return metadata;
	}

	void setMetadata(ModMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public ModState getState() {
		return state;
	}

	void setClassLoader(ModClassLoader classLoader) {
        if(this.classLoader != null)
            throw new IllegalStateException("Class loader has already set!");
        this.classLoader = classLoader;
    }

    void setInstance(Object instance) {
        this.instance = instance;
    }
}
