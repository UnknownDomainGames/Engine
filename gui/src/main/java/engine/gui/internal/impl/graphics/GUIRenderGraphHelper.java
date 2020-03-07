package engine.gui.internal.impl.graphics;

import engine.graphics.graph.*;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.BlendMode;
import engine.graphics.util.CullMode;
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
                colorBuffer.setFormat(TextureFormat.RGB8);
                colorBuffer.setRelativeSize(1, 1);

                RenderBufferInfo depthBuffer = RenderBufferInfo.renderBuffer();
                depthBuffer.setName("depth");
                depthBuffer.setFormat(TextureFormat.DEPTH24);
                depthBuffer.setRelativeSize(1, 1);

                mainTask.setRenderBuffers(colorBuffer, depthBuffer);
            }
            {
                RenderPassInfo guiPass = RenderPassInfo.renderPass();
                guiPass.setName("gui");
                guiPass.setCullMode(CullMode.CULL_BACK);
                guiPass.setColorOutputs(colorOutput()
                        .setColorBuffer("color")
                        .setClear(true)
                        .setClearColor(stage.getScene().getFill())
                        .setBlendMode(BlendMode.MIX));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("gui");
                    sceneDrawer.setDrawDispatcher(new StageDrawDispatcher(stage));
                    guiPass.setDrawers(sceneDrawer);
                }
                mainTask.setPasses(guiPass);
            }
            renderGraph.setTasks(mainTask);
        }
        return renderGraph;
    }

    private GUIRenderGraphHelper() {
    }
}
