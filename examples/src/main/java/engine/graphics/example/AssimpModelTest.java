package engine.graphics.example;

import engine.graphics.GraphicsEngine;
import engine.graphics.application.Application3D;
import engine.graphics.display.Window;
import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.texture.GLColorFormat;
import engine.graphics.gl.texture.GLTextureBuffer;
import engine.graphics.graph.*;
import engine.graphics.internal.graph.ViewportOpaqueDrawDispatcher;
import engine.graphics.internal.graph.ViewportSkyDrawDispatcher;
import engine.graphics.internal.graph.ViewportTransparentDrawDispatcher;
import engine.graphics.model.assimp.AssimpHelper;
import engine.graphics.model.assimp.AssimpModelDrawDispatcher;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformImage;
import engine.graphics.shape.Box;
import engine.graphics.shape.Line;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.BlendMode;
import engine.graphics.util.CullMode;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.graphics.viewport.Viewport;
import engine.input.Action;
import engine.input.KeyCode;
import engine.util.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL42;

import java.lang.management.ManagementFactory;

import static engine.graphics.graph.ColorOutputInfo.colorOutput;
import static engine.graphics.graph.DepthOutputInfo.depthOutput;

public class AssimpModelTest extends Application3D {
    private FlyCameraInput cameraInput;

    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    protected void onInitialized() {
        Window window = manager.getPrimaryWindow();
        mainViewPort.hide();
        mainViewPort.show(createRenderGraph(mainViewPort)).bindWindow(window);
        window.addWindowCloseCallback(win -> stop());
        window.addKeyCallback((win, key, scancode, action, mods) -> {
            if (key == KeyCode.ESCAPE && action == Action.PRESS) stop();
        });

        cameraInput = new FlyCameraInput(mainViewPort.getCamera());
        cameraInput.bindWindow(window);

        var lineX = new Line(new Vector3f(0, 0, 0), new Vector3f(128, 0, 0), Color.RED);
        var lineY = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 128, 0), Color.GREEN);
        var lineZ = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 128), Color.BLUE);
        var box = new Box(new Vector3f(0, 0, 0), 1, Color.BLUE);
        box.setTranslation(0, 0, -5);
        var box2 = new Box(new Vector3f(0, 0, 0), 1, Color.fromARGB(0x7fff0000));
        box2.setTranslation(0, 0, 5);
        box2.setRenderType(RenderType.TRANSLUCENT);
        var box3 = new Box(new Vector3f(0, 0, 0), 1, Color.fromARGB(0x7f00ff00));
        box3.setTranslation(1, 0, 3);
        box3.setRenderType(RenderType.TRANSLUCENT);
        var assimpTest = AssimpHelper.loadModel("/boblamp.fbx");
        assimpTest.getMaterials().values().forEach(mat -> mat.getEngineMaterial().setAmbient(Color.WHITE));
        GraphicsEngine.getAnimationManager().play("assimp-test", true, assimpTest.getCurrentAnimation());
        mainScene.addNode(lineX, lineY, lineZ, box, box2, box3);
        mainScene.addNode(assimpTest);
        mainScene.getLightManager().setAmbientLight(1, 1, 1);

        window.setSize(854, 480);
        window.centerOnScreen();
        window.getCursor().disableCursor();
        System.out.println("Hello World!");
    }

    @Override
    protected void onPreRender() {
        cameraInput.update(ticker.getTpf());
    }

    public static RenderGraphInfo createRenderGraph(Viewport viewport) {
        RenderGraphInfo renderGraph = RenderGraphInfo.renderGraph();
        renderGraph.setMainTask("main");
        {
            var linkedListHeaderImage = Texture2D.builder().format(ColorFormat.RED32UI).minFilter(FilterMode.NEAREST).magFilter(FilterMode.NEAREST)
                    .build(1920, 1080);//TODO: get client screen max size
            //TODO: generalize all the buffers below
            var headerClearBuffer = new GLVertexBuffer(GLBufferType.PIXEL_UNPACK_BUFFER, GLBufferUsage.STATIC_DRAW);
            headerClearBuffer.allocateSize(1920 * 1080 * 4);
            var atomicCounterBuffer = new GLVertexBuffer(GLBufferType.ATOMIC_COUNTER_BUFFER, GLBufferUsage.DYNAMIC_COPY);
            atomicCounterBuffer.allocateSize(4);

            var linkedListBuffer = new GLVertexBuffer(GLBufferType.TEXTURE_BUFFER, GLBufferUsage.DYNAMIC_COPY);
            linkedListBuffer.allocateSize(1920 * 1080 * 3 * 16);
            var linkedListMimicImage = new GLTextureBuffer(GLColorFormat.RGBA32UI, linkedListBuffer);

            RenderTaskInfo mainTask = RenderTaskInfo.renderTask();
            mainTask.setName("main");
            mainTask.setFinalPass("oitDraw");
            mainTask.addSetup((frameContext, renderTask) -> {
                Frame frame = frameContext.getFrame();
                if (frame.isResized()) viewport.setSize(frame.getOutputWidth(), frame.getOutputHeight());
                viewport.getScene().doUpdate(frame.getTimeToLastUpdate());
                atomicCounterBuffer.bind();
                var mapBuffer = GL15.glMapBuffer(GL42.GL_ATOMIC_COUNTER_BUFFER, GL15.GL_WRITE_ONLY);
                mapBuffer.putInt(0);
                mapBuffer.flip();
                GL15.glUnmapBuffer(GL42.GL_ATOMIC_COUNTER_BUFFER);
                atomicCounterBuffer.unbind();
//                atomicCounterBuffer.uploadData(new int[]{0});
                headerClearBuffer.bind();
                linkedListHeaderImage.upload(0, 0, 0, frame.getOutputWidth(), frame.getOutputHeight(), null);
                headerClearBuffer.unbind();
            });
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

                RenderPassInfo assimpPass = RenderPassInfo.renderPass();
                assimpPass.setName("assimp");
                assimpPass.dependsOn("opaque");
                assimpPass.setCullMode(CullMode.CULL_BACK);
                assimpPass.addColorOutputs(colorOutput().setColorBuffer("color"));
                assimpPass.setDepthOutput(depthOutput().setDepthBuffer("depth").setWritable(false));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("assimp_model");
                    sceneDrawer.setDrawDispatcher(new AssimpModelDrawDispatcher(viewport, linkedListHeaderImage, linkedListMimicImage, atomicCounterBuffer));
                    assimpPass.addDrawers(sceneDrawer);
                }

                RenderPassInfo transparentPass = RenderPassInfo.renderPass();
                transparentPass.setName("transparent");
                transparentPass.dependsOn("opaque");
                transparentPass.setCullMode(CullMode.CULL_BACK);
                transparentPass.addColorOutputs(colorOutput().setColorBuffer("color"));
                transparentPass.setDepthOutput(depthOutput().setDepthBuffer("depth").setWritable(false));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("transparent");
                    sceneDrawer.setDrawDispatcher(new ViewportTransparentDrawDispatcher(viewport, linkedListHeaderImage, linkedListMimicImage, atomicCounterBuffer));
                    transparentPass.addDrawers(sceneDrawer);
                }

                RenderPassInfo transparentDrawOITPass = RenderPassInfo.renderPass();
                transparentDrawOITPass.setName("oitDraw");
                transparentDrawOITPass.dependsOn("assimp", "transparent");
                transparentDrawOITPass.addColorOutputs(colorOutput().setColorBuffer("color").setBlendMode(BlendMode.MIX));
                transparentDrawOITPass.setDepthOutput(depthOutput().setDepthBuffer("depth"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("OITDraw");
                    sceneDrawer.setDrawDispatcher(new DrawDispatcher() {

                        private UniformImage uniformListBuffer;
                        private UniformImage listHeader;

                        @Override
                        public void init(Drawer drawer) {
                            ShaderResource resource = drawer.getShaderResource();
                            listHeader = resource.getUniformImage("linkedListHeader");
                            uniformListBuffer = resource.getUniformImage("linkedListBuffer");
                            uniformListBuffer.getBinding().setCanWrite(false);
                        }

                        @Override
                        public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
                            ShaderResource resource = drawer.getShaderResource();
                            listHeader.set(linkedListHeaderImage);
                            uniformListBuffer.set(linkedListMimicImage);
                            resource.refresh();
                            var dataBuf = VertexDataBuf.currentThreadBuffer();
                            dataBuf.begin(VertexFormat.POSITION);
                            dataBuf.pos(-1, -1, 0).endVertex();
                            dataBuf.pos(1, -1, 0).endVertex();
                            dataBuf.pos(-1, 1, 0).endVertex();

                            dataBuf.pos(-1, 1, 0).endVertex();
                            dataBuf.pos(1, -1, 0).endVertex();
                            dataBuf.pos(1, 1, 0).endVertex();
                            dataBuf.finish();
                            renderer.drawStreamed(DrawMode.TRIANGLES, dataBuf);
                        }
                    });
                    transparentDrawOITPass.addDrawers(sceneDrawer);
                }

                mainTask.addPasses(skyPass, opaquePass, assimpPass, transparentPass, transparentDrawOITPass);
            }
            renderGraph.addTasks(mainTask);
        }
        return renderGraph;
    }

}
