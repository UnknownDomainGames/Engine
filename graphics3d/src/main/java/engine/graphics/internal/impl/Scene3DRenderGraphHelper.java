package engine.graphics.internal.impl;

import engine.graphics.graph.*;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.CullMode;
import engine.graphics.viewport.Viewport;

import static engine.graphics.graph.ColorOutputInfo.colorOutput;
import static engine.graphics.graph.DepthOutputInfo.depthOutput;

public final class Scene3DRenderGraphHelper {

    public static RenderGraphInfo createRenderGraph(Viewport viewport) {
        RenderGraphInfo renderGraph = RenderGraphInfo.renderGraph();
        renderGraph.setMainTask("main");
        {
            RenderTaskInfo mainTask = RenderTaskInfo.renderTask();
            mainTask.setName("main");
            mainTask.setFinalPass("opaque");
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
                RenderPassInfo opaquePass = RenderPassInfo.renderPass();
                opaquePass.setName("opaque");
                opaquePass.setCullMode(CullMode.CULL_BACK);
                opaquePass.setColorOutputs(colorOutput().setClear(true).setColorBuffer("color"));
                opaquePass.setDepthOutput(depthOutput().setClear(true).setDepthBuffer("depth"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("opaque");
                    sceneDrawer.setDrawDispatcher(new ViewportOpaqueDrawDispatcher(viewport));
                    opaquePass.setDrawers(sceneDrawer);
                }
                mainTask.setPasses(opaquePass);
            }
            renderGraph.setTasks(mainTask);
        }
        return renderGraph;
    }

    private Scene3DRenderGraphHelper() {
    }
}
