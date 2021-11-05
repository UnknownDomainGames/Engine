package engine.graphics.example;

import engine.graphics.application.Application3D;
import engine.graphics.display.Window;
import engine.graphics.shape.Box;
import engine.graphics.shape.Line;
import engine.input.Action;
import engine.input.KeyCode;
import engine.util.Color;
import org.joml.Vector3f;

import java.lang.management.ManagementFactory;

public class Hello3D extends Application3D {

    private FlyCameraInput cameraInput;

    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    protected void onInitialized() {
        Window window = manager.getPrimaryWindow();
        window.addWindowCloseCallback(win -> stop());
        window.addKeyCallback((win, key, scancode, action, mods) -> {
            if (key == KeyCode.ESCAPE && action == Action.PRESS) stop();
        });

        cameraInput = new FlyCameraInput(mainViewPort.getCamera());
        cameraInput.bindWindow(window);

        var lineX = new Line(new Vector3f(0, 0, 0), new Vector3f(128, 0, 0), Color.RED);
        var lineY = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 128, 0), Color.GREEN);
        var lineZ = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 128), Color.BLUE);
        var box = new Box(new Vector3f(0, 0, 0), 1, Color.BLUE);
        box.setTranslation(0, 0, -5);
        var box2 = new Box(new Vector3f(0, 0, 0), 1, Color.WHITE);
        box2.setTranslation(.5f, .5f, .5f);
        mainScene.addNode(lineX, lineY, lineZ, box, box2);

        window.setSize(854, 480);
        window.centerOnScreen();
        window.disableCursor();
        System.out.println("Hello World!");
    }

    @Override
    protected void onPreRender() {
        cameraInput.update(ticker.getTpf());
    }
}
