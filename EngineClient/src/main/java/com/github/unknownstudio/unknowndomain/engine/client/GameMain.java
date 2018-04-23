package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.window.WindowDisplay;

public class GameMain {
	
    private WindowDisplay window;

    public GameMain(int width, int height) {
        window = new WindowDisplay(width ,height, "Known Domain");
        window.init();
        gameLoop();
    }

    public void gameLoop() {
        while (!window.shouldClose()) {
            window.update();
        }
    }
}
