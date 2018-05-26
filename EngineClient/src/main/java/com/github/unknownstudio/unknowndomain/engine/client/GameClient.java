package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.display.WindowDisplay;
import com.github.unknownstudio.unknowndomain.engine.client.render.RenderGlobal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

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

    public void handleCursorMove(double x, double y){
        renderer.onCursorMoved(x,y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers){
        if (action == GLFW.GLFW_PRESS){
            double moveC = 0.05;
            switch (key){
                case GLFW.GLFW_KEY_W:
                    renderer.getCamera().move(0,0,-1 * moveC);
                    break;
                case GLFW.GLFW_KEY_S:
                    renderer.getCamera().move(0,0,1 * moveC);
                    break;
                case GLFW.GLFW_KEY_A:
                    renderer.getCamera().move(-1 * moveC,0,0);
                    break;
                case GLFW.GLFW_KEY_D:
                    renderer.getCamera().move(1 * moveC,0,0);
                    break;
            }
        }
    }

    public void handleTextInput(int codepoint, int modifiers){}

    public void handleMousePress(int button, int action, int modifiers){

    }

    public RenderGlobal getRenderer() {
        return renderer;
    }

    public void handleScroll(double xoffset, double yoffset) {
        //renderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }
}
