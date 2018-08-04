package unknowndomain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;

/**
 * Engine Platform
 */
public class Platform {
	
    private static final Logger LOGGER = LoggerFactory.getLogger("Engine");
    private static Engine engine; // TODO inject

	public static Logger getLogger() {
		return LOGGER;
	}

	public static Engine getEngine() {
		return engine;
	}

    public static Locale getLocale() {
	    return Locale.getDefault(); // TODO Game locale
    }
    
	public boolean isDevEnv() {
		return true; // TODO
	}
	
	public boolean isClient() {
		return true; // TODO
	}
	
	public boolean isServer() {
		return true; // TODO
	}
}
