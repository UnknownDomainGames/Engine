package engine.gui.internal.impl.graphics;

import engine.graphics.graph.*;
import engine.graphics.texture.ColorFormat;
import engine.graphics.util.BlendMode;
import engine.gui.stage.Stage;

import static engine.graphics.graph.ColorOutputInfo.colorOutput;

public final class GUIRenderGraphHelper {

    public static RenderGraphInfo createRenderGraph(Stage stage) {
        RenderGraphInfo renderGraph = RenderGraphInfo.renderGraph();
        renderGraph.setMainTask("main");
        {
            RenderTaskInfo mainTask = RenderTaskInfo.renderTask();
            mainTask.setName("main");
            mainTask.setFinalPass("gui");
            {
                RenderBufferInfo colorBuffer = RenderBufferInfo.renderBuffer();
                colorBuffer.setName("color");
                colorBuffer.setFormat(ColorFormat.RGB8);
                colorBuffer.setRelativeSize(1, 1);

                RenderBufferInfo depthBuffer = RenderBufferInfo.renderBuffer();
                depthBuffer.setName("depth");
                depthBuffer.setFormat(ColorFormat.DEPTH24);
                depthBuffer.setRelativeSize(1, 1);

                mainTask.addRenderBuffers(colorBuffer, depthBuffer);
            }
            {
                RenderPassInfo guiPass = RenderPassInfo.renderPass();
                guiPass.setName("gui");
                guiPass.addColorOutputs(colorOutput()
                        .setColorBuffer("color")
                        .setClear(true)
                        .setClearColor(stage.getScene().getFill())
                        .setBlendMode(BlendMode.MIX));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("gui");
                    sceneDrawer.setDrawDispatcher(new StageDrawDispatcher(stage));
                    guiPass.addDrawers(sceneDrawer);
                }
                mainTask.addPasses(guiPass);
            }
            renderGraph.addTasks(mainTask);
        }
        return renderGraph;
    }

    private GUIRenderGraphHelper() {
    }
}
