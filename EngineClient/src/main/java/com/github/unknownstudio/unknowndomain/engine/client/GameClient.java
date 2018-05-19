package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.display.WindowDisplay;
import com.github.unknownstudio.unknowndomain.engine.client.render.RenderGlobal;

public class GameClient implements com.github.unknownstudio.unknowndomain.engineapi.client.game.GameClient{
	
    private WindowDisplay window;
    private RenderGlobal renderer;

    public GameClient(int width, int height) {
        window = new WindowDisplay(this, width ,height, UnknownDomain.getName());
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
