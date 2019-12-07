package nullengine.client.rendering.example;

import nullengine.client.rendering.application.RenderableApplication;
import nullengine.client.rendering.camera.FreeCamera;
import nullengine.client.rendering.gl.shape.Box;
import nullengine.client.rendering.gl.shape.Pane;
import nullengine.client.rendering.scene.Geometry;
import nullengine.util.Color;
import org.joml.Vector2f;
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
            var camera = this.mainViewPort.getCamera();
            if (camera instanceof FreeCamera) {
                switch (key) {
                    case GLFW.GLFW_KEY_S:
                        ((FreeCamera) camera).moveAt(new Vector3f(0, 0, 0.02f));
                        break;
                    case GLFW.GLFW_KEY_W:
                        ((FreeCamera) camera).moveAt(new Vector3f(0, 0, -0.02f));
                        break;
                    case GLFW.GLFW_KEY_A:
                        ((FreeCamera) camera).moveAt(new Vector3f( -0.02f,0, 0));
                        break;
                    case GLFW.GLFW_KEY_D:
                        ((FreeCamera) camera).moveAt(new Vector3f( 0.02f,0, 0));
                        break;
                    case GLFW.GLFW_KEY_SPACE:
                        ((FreeCamera) camera).moveAt(new Vector3f(0, 0.02f,0));
                        break;
                    case GLFW.GLFW_KEY_LEFT_SHIFT:
                        ((FreeCamera) camera).moveAt(new Vector3f(0, -0.02f, 0));
                        break;

                }
            }

        });

        Geometry geometry = new Geometry(new Pane(new Vector2f(1, 0.5f), new Vector2f(-1, -0.5f), Color.WHITE));
        mainScene.getChildren().add(geometry);
        mainScene.getChildren().add(new Geometry(new Box(new Vector3f(1, 0.5f, -10), new Vector3f(-1, -0.5f, -5), Color.WHITE)));
        System.out.println("Ready!");
    }

    @Override
    protected void onPostRender() {
        GLFW.glfwPollEvents();
    }
}
