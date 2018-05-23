package com.github.unknownstudio.unknowndomain.engine.client;

public class UnknownDomain {
	
	private static final String NAME = "${project.name}";
	private static final String VERSION = "${project.version}";
	
    public static final int WIDTH = 854, HEIGHT = 480;
    private static GameClient game;

    public static void main(String[] args) {
        game = new GameClient(WIDTH, HEIGHT);
    }
    
    public static String getName() {
    	return NAME;
    }
    
    public static String getVersion() {
    	return VERSION;
    }
}
