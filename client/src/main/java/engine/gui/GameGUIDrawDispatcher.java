package engine.gui;

import engine.graphics.display.Window;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.shader.ShaderResource;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.graphics.GraphicsImpl;
import engine.gui.stage.Stage;

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
        Window window = StageHelper.getWindow(stage);
        int width = frame.getWidth(), height = frame.getHeight();
        if (stage.getWidth() != window.getWidth() ||
                stage.getHeight() != window.getHeight()) {
            StageHelper.setViewport(stage, frame.getWidth(), frame.getHeight(),
                    window.getContentScaleX(), window.getContentScaleY());
        }

        Scene scene = stage.getScene();
        if (scene == null) return;
        scene.update();

        Parent root = scene.getRoot();
        if (!root.isVisible()) {
            return; // Invisible root, don't need render it.
        }

        graphics.setup(renderer, width, height, stage.getScaleX(), stage.getScaleY());

        root.getRenderer().render(root, graphics);
    }
}
