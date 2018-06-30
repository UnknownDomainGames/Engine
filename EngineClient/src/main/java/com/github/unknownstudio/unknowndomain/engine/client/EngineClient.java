package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.display.DefaultGameWindow;
import com.github.unknownstudio.unknowndomain.engine.client.rendering.RendererGame;
import com.github.unknownstudio.unknowndomain.engine.client.rendering.RendererGlobal;
import com.github.unknownstudio.unknowndomain.engineapi.math.Timer;
import org.lwjgl.glfw.GLFW;

public class EngineClient implements com.github.unknownstudio.unknowndomain.engineapi.client.GameClient{
	
    private DefaultGameWindow window;
    private RendererGlobal renderer;

    private Timer timer;

    public EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width ,height, UnknownDomain.getName());

        window.init();
        init();
        gameLoop();
    }

    @Override
    public void init() {
        renderer = new RendererGlobal();
        timer = new Timer();
        timer.init();
    }

    @Override
    public void loop() {

    }

    @Override
    public void terminate() {

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
        renderer.getRendererGame().onCursorMoved(x,y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers){
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS){
            GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
        }
        renderer.getRendererGame().getCamera().handleMove(key,action);
    }

    public void handleTextInput(int codepoint, int modifiers){}

    public void handleMousePress(int button, int action, int modifiers){

    }

    public RendererGlobal getRenderer() {
        return renderer;
    }

    public void handleScroll(double xoffset, double yoffset) {
        //renderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }
}
