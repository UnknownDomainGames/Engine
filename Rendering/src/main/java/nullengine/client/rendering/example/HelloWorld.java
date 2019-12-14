package nullengine.client.rendering.example;

import nullengine.client.rendering.application.RenderableApplication;
import nullengine.client.rendering.gl.shape.Box;
import nullengine.client.rendering.gl.shape.Line;
import nullengine.client.rendering.scene.Geometry;
import nullengine.util.Color;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.lang.management.ManagementFactory;

public class HelloWorld extends RenderableApplication {

    private FlyCameraInput cameraInput;

    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    protected void onInitialized() {
        manager.getPrimaryWindow().addWindowCloseCallback(window -> stop());
        manager.getPrimaryWindow().addKeyCallback((window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) stop();
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

    @Override
    protected void onPostRender() {
        GLFW.glfwPollEvents();
    }
}
