package unknowndomain.engine.mod.java;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.ModState;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;

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

    private HarvestedInfo harvestedInfo;

    public JavaModContainer(String modid, Path source){
        this.modid = modid;
        this.logger = LoggerFactory.getLogger(modid);
        this.source = source;
    }

    @Override
	public String getModId() {
		return modid;
	}

	@Override
	public Object getInstance() {
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

	@Override
	public ModMetadata getMetadata() {
		return metadata;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public ModState getState() {
		return state;
	}

	void initialize(ModClassLoader classLoader, ModMetadata metadata, HarvestedInfo harvestedInfo, Object instance) {
		if(this.classLoader != null)
			throw new IllegalStateException("Mod has already initilaized!");
		this.classLoader = classLoader;
		this.metadata = metadata;
		this.harvestedInfo = harvestedInfo;
		this.instance = instance;
	}
}
