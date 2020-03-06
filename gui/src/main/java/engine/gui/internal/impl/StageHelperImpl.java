package engine.gui.internal.impl;

import engine.graphics.GraphicsEngine;
import engine.graphics.backend.GraphicsBackend;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.graph.RenderGraph;
import engine.gui.Stage;
import engine.gui.internal.StageHelper;
import engine.gui.internal.impl.graphics.GUIRenderGraphHelper;

import java.util.HashMap;
import java.util.Map;

public final class StageHelperImpl extends StageHelper {

    private final GraphicsBackend graphicsBackend;
    private final WindowHelper windowHelper;

    private final Map<Stage, StageInputHelper> stageInputHandlerMap = new HashMap<>();
    private final Map<Stage, RenderGraph> stageRenderGraphMap = new HashMap<>();

    private Stage primaryStage;
    private Window primaryWindow;

    public StageHelperImpl() {
        graphicsBackend = GraphicsEngine.getGraphicsBackend();
        windowHelper = graphicsBackend.getWindowHelper();
        primaryStage = new Stage();
        primaryWindow = graphicsBackend.getPrimaryWindow();
        setPrimary(primaryStage, true);
    }

    @Override
    public void show(Stage stage) {
        if (stage.isShowing()) throw new IllegalStateException("Stage is showing");
        Window window = getWindow(stage);
        if (window == null) {
            Stage owner = stage.getOwner();
            window = isPrimary(stage) ? primaryWindow : windowHelper.createWindow(owner == null ? null : getWindow(owner));
            setWindow(stage, window);
        }
        window.show();
        getShowingProperty(stage).set(true);
        doVisibleChanged(stage, true);
        enableInput(stage);
        enableRender(stage);
    }

    @Override
    public void hide(Stage stage) {
        if (!stage.isShowing()) throw new IllegalStateException("Stage is hiding");
        Window window = getWindow(stage);
        window.hide();
        disableRender(stage);
        disableInput(stage);
        getShowingProperty(stage).set(false);
        doVisibleChanged(stage, false);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void enableInput(Stage stage) {
        StageInputHelper stageInputHelper = new StageInputHelper(stage);
        stageInputHelper.enable();
        stageInputHandlerMap.put(stage, stageInputHelper);
    }

    public void disableInput(Stage stage) {
        StageInputHelper inputHandler = stageInputHandlerMap.remove(stage);
        if (inputHandler != null) inputHandler.disable();
    }

    public void enableRender(Stage stage) {
        RenderGraph renderGraph = graphicsBackend.loadRenderGraph(GUIRenderGraphHelper.createRenderGraph(stage));
        renderGraph.bindWindow(getWindow(stage));
        stageRenderGraphMap.put(stage, renderGraph);
    }

    public void disableRender(Stage stage) {
        graphicsBackend.removeRenderGraph(stageRenderGraphMap.get(stage));
    }
}
