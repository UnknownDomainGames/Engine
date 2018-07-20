package unknowndomain.engine.mod;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.mod.ModMetadata;
import unknowndomain.engine.api.util.versioning.ComparableVersion;
//TODO: collect mod's class loader, instance of mod main class, mod config, mod looger, config dir.
public class JavaModContainer implements ModContainer {
	
    private final String modid;
    private final ComparableVersion version;

    /**
     * Class loader of the mod.
     */
    private ModClassLoader classLoader;
    
    private final Logger logger;
    
    private Object instance;
    
    private ModMetadata metadata;

    private final Path source;

    public JavaModContainer(String modid, String version, Path src){
        this.modid = modid;
        this.version = new ComparableVersion(version);
        this.logger = LoggerFactory.getLogger(modid);
        source = src;
    }

    @Override
	public String getModId() {
		return modid;
	}

    @Override
	public ComparableVersion getVersion() {
		return version;
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

	public void setMetadata(ModMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

    public void setClassLoader(ModClassLoader classLoader) {
        if(this.classLoader != null)
            throw new IllegalStateException("Class loader has already set!");
        this.classLoader = classLoader;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
