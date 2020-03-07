package engine.gui;

import engine.graphics.display.Window;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.shader.ShaderResource;
import engine.gui.internal.SceneHelper;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.graphics.GraphicsImpl;

public class GameGUIDrawDispatcher implements DrawDispatcher {
    private final Stage guiStage;
    private final Stage hudStage;

    private GraphicsImpl graphics;

    public GameGUIDrawDispatcher(Stage guiStage, Stage hudStage) {
        this.guiStage = guiStage;
        this.hudStage = hudStage;
    }

    @Override
    public void init(ShaderResource resource) {
        graphics = new GraphicsImpl(resource);
    }

    @Override
    public void draw(Frame frame, ShaderResource resource, Renderer renderer) {
        Stage stage = guiStage.getScene() != null ? guiStage : hudStage;

        Scene scene = stage.getScene();
        if (scene == null) return;

        Window window = StageHelper.getWindow(stage);
        int width = frame.getWidth(), height = frame.getHeight();
        if (SceneHelper.getViewportWidth(scene) != window.getWidth() ||
                SceneHelper.getViewportHeight(scene) != window.getHeight()) {
            SceneHelper.setViewport(scene, frame.getWidth(), frame.getHeight(),
                    window.getContentScaleX(), window.getContentScaleY());
        }
        scene.update();

        Parent root = scene.getRoot();
        if (!root.isVisible()) {
            return; // Invisible root, don't need render it.
        }

        graphics.setup(renderer, width, height, scene.getScaleX(), scene.getScaleY());

        root.getRenderer().render(root, graphics);
//        for (Popup popup : scene.getPopups()) {
//            popup.getRenderer().render(popup, graphics);
//        }
    }
}
