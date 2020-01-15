package nullengine.client.rendering3d.example;

import nullengine.client.rendering.gl.shape.Box;
import nullengine.client.rendering.gl.shape.Line;
import nullengine.client.rendering3d.Geometry;
import nullengine.client.rendering3d.application.Application3D;
import nullengine.input.Action;
import nullengine.input.KeyCode;
import nullengine.util.Color;
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
        manager.getPrimaryWindow().addWindowCloseCallback(window -> stop());
        manager.getPrimaryWindow().addKeyCallback((window, key, scancode, action, mods) -> {
            if (key == KeyCode.KEY_ESCAPE && action == Action.PRESS) stop();
        });

        cameraInput = new FlyCameraInput(mainViewPort.getCamera());
        cameraInput.bindWindow(manager.getPrimaryWindow());

        var lineX = new Geometry(new Line(new Vector3f(0, 0, 0), new Vector3f(128, 0, 0), Color.RED));
        var lineY = new Geometry(new Line(new Vector3f(0, 0, 0), new Vector3f(0, 128, 0), Color.GREEN));
        var lineZ = new Geometry(new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 128), Color.BLUE));
        var box = new Geometry(new Box(new Vector3f(0, 0, 0), 1, Color.BLUE));
        box.setTranslation(0, 0, -5);
        var box2 = new Geometry(new Box(new Vector3f(0, 0, 0), 1, Color.WHITE));
        box2.setTranslation(.5f, .5f, .5f);
        mainScene.addNode(lineX, lineY, lineZ, box, box2);

        manager.getPrimaryWindow().getCursor().disableCursor();
        System.out.println("Hello World!");
    }

    @Override
    protected void onPreRender() {
        cameraInput.update(ticker.getTpf());
    }
}
