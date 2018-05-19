package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.render.RenderGlobal;
import com.github.unknownstudio.unknowndomain.engine.client.window.WindowDisplay;

public class GameMain {
	
    private WindowDisplay window;
    private RenderGlobal renderer;

    public GameMain(int width, int height) {
        window = new WindowDisplay(this, width ,height, "Known Domain");
        window.init();
        renderer = new RenderGlobal();
        gameLoop();
    }

    public void gameLoop() {
        while (!window.shouldClose()) {
            window.update();
        }
    }

    public RenderGlobal getRenderer() {
        return renderer;
    }
}
