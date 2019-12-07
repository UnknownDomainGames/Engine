package nullengine.client.rendering.example;

import nullengine.client.rendering.application.RenderableApplication;
import nullengine.client.rendering.gl.shape.Line;
import nullengine.client.rendering.scene.Geometry;
import nullengine.util.Color;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.lang.management.ManagementFactory;

public class HelloWorld extends RenderableApplication {
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

//        mainViewPort.setClearColor(Color.RED);
        Geometry geometry = new Geometry(new Line(new Vector3f(100, 100, -1), new Vector3f(-100, 100, -1), Color.WHITE));
        mainScene.getChildren().add(geometry);
        System.out.println("Ready!");
    }

    @Override
    protected void onPostRender() {
        GLFW.glfwPollEvents();
    }
}
