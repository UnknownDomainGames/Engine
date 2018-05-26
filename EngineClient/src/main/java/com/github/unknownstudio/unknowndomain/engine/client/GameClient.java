package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.display.WindowDisplay;
import com.github.unknownstudio.unknowndomain.engine.client.render.RenderGlobal;
import com.github.unknownstudio.unknowndomain.engineapi.math.Timer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class GameClient implements com.github.unknownstudio.unknowndomain.engineapi.client.game.GameClient{
	
    private WindowDisplay window;
    private RenderGlobal renderer;

    private Timer timer;

    public GameClient(int width, int height) {
        window = new WindowDisplay(this, width ,height, UnknownDomain.getName());
        window.init();
        renderer = new RenderGlobal();
        timer = new Timer();
        timer.init();
        gameLoop();
    }

    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / 30.0f;
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                //update(interval); //TODO: game logic
                accumulator -= interval;
            }

            window.update();

            sync(); //TODO: check if use v-sync first
        }
    }

    private void sync() {
        float loopSlot = 1f / 60.0f;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    public void handleCursorMove(double x, double y){
        renderer.onCursorMoved(x,y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers){
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS){
            GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
        }
        renderer.getCamera().handleMove(key,action);
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
