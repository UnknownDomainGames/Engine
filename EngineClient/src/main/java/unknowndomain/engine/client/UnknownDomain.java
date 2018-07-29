package unknowndomain.engine.client;

import org.apache.commons.lang3.SystemUtils;

public class UnknownDomain {
	
	private static final String NAME = "${project.name}";
	private static final String VERSION = "${project.version}";
	
    public static final int WIDTH = 854, HEIGHT = 480;
    private static EngineClient engine;

    public static void main(String[] args) {
        if(SystemUtils.IS_OS_MAC){ //TODO: require review: where should we put this OS checking
            System.setProperty(SystemUtils.JAVA_AWT_HEADLESS, "true");
        }
        engine = new EngineClient(WIDTH, HEIGHT);
        engine.init();
    }

    public static EngineClient getEngine() {
        return engine;
    }

    public static String getName() {
    	return NAME;
    }
    
    public static String getVersion() {
    	return VERSION;
    }
}
