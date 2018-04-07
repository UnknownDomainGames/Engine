package com.github.unknownstudio.knowndomain.game;

import com.github.unknownstudio.knowndomain.game.key.KeyBinding;
import com.github.unknownstudio.knowndomain.game.renderer.Renderer;

import java.util.ArrayList;

public class GameMain {
    WindowDisplay window;

    public static final long FPS = 1000L / 60L;

    public ArrayList<KeyBinding> keyBindings;
    public ArrayList<Renderer> renderers;

    public GameMain(int width, int height) {
        window = new WindowDisplay(width ,height, "Known Domain");
        keyBindings = new ArrayList<>();
        window.init();
        gameLoop();
    }

    public void gameLoop() {
        while (!window.shouldClose()) {
            handleInput();
            update();
            handleRender();
            sleep();
        }
    }

    public void handleInput() {
        for(KeyBinding keyBinding : keyBindings) {
            if(window.getKey(keyBinding.BindKey(), keyBinding.BindAction())) {
                keyBinding.handle(this);
            }
        }
    }

    public void handleRender() {
        for(Renderer renderer : renderers) {
            renderer.render(window);
        }
    }

    public void update() {

    }

    public void sleep(){
        try{
            Thread.sleep(FPS);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
