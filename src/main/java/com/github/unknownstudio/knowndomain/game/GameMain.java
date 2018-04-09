package com.github.unknownstudio.knowndomain.game;

import com.github.unknownstudio.knowndomain.engine.client.WindowDisplay;

public class GameMain {
    WindowDisplay window;

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
