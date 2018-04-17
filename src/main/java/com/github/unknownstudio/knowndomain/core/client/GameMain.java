package com.github.unknownstudio.knowndomain.core.client;

import com.github.unknownstudio.knowndomain.core.client.render.WindowDisplay;

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
