package unknowndomain.engine.mod;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.util.versioning.ComparableVersion;
//TODO: collect mod's class loader, instance of mod main class, mod config, mod looger, config dir.
public class JavaModContainer implements ModContainer {
	
    private final String modid;
    private final ComparableVersion version;
    
    private final Logger logger;
    
    private Object instance;

    public JavaModContainer(String modid, String version){
        this.modid = modid;
        this.version = new ComparableVersion(version);
        this.logger = LoggerFactory.getLogger(modid);
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
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public boolean isEnable() {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public void setEnable(boolean enable) {
		// TODO 自动生成的方法存根
		
	}
}
