package engine.graphics.internal.graph;

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

                mainTask.addRenderBuffers(colorBuffer, depthBuffer);
            }
            {
                RenderPassInfo skyPass = RenderPassInfo.renderPass();
                skyPass.setName("sky");
                skyPass.setCullMode(CullMode.CULL_BACK);
                skyPass.addColorOutputs(colorOutput().setClear(true).setColorBuffer("color"));
                skyPass.setDepthOutput(depthOutput().setClear(true).setWritable(false).setDepthBuffer("depth"));
                {
                    DrawerInfo skyDrawer = DrawerInfo.drawer();
                    skyDrawer.setShader("sky");
                    skyDrawer.setDrawDispatcher(new ViewportSkyDrawDispatcher(viewport));
                    skyPass.addDrawers(skyDrawer);
                }

                RenderPassInfo opaquePass = RenderPassInfo.renderPass();
                opaquePass.setName("opaque");
                opaquePass.dependsOn("sky");
                opaquePass.setCullMode(CullMode.CULL_BACK);
                opaquePass.addColorOutputs(colorOutput().setColorBuffer("color"));
                opaquePass.setDepthOutput(depthOutput().setDepthBuffer("depth"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("opaque");
                    sceneDrawer.setDrawDispatcher(new ViewportOpaqueDrawDispatcher(viewport));
                    opaquePass.addDrawers(sceneDrawer);
                }
                mainTask.addPasses(skyPass, opaquePass);
            }
            renderGraph.addTasks(mainTask);
        }
        return renderGraph;
    }

    private Scene3DRenderGraphHelper() {
    }
}
