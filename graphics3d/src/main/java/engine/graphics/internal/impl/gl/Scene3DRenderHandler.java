package engine.graphics.internal.impl.gl;

import com.github.mouse0w0.observable.value.ObservableObjectValue;
import engine.graphics.display.Window;
import engine.graphics.gl.shader.ShaderManager;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.management.RenderHandler;
import engine.graphics.queue.GeometryList;
import engine.graphics.queue.RenderQueue;
import engine.graphics.queue.RenderTypeHandler;
import engine.graphics.queue.StandardRenderTypes;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.viewport.Viewport;
import engine.util.Color;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class Scene3DRenderHandler implements RenderHandler {

    private final List<Viewport> viewports;

    private final ObservableObjectValue<ShaderProgram> shader;

    private GraphicsBackend manager;

//    private final Material material;

    public Scene3DRenderHandler(List<Viewport> viewports) {
        this.viewports = viewports;
        this.shader = ShaderManager.register("example");

//        material = new Material()
//                .setAmbientColor(new Vector3f(0.5f))
//                .setDiffuseColor(new Vector3f(1.0f))
//                .setSpecularColor(new Vector3f(1.0f))
//                .setShininess(32f);
    }

    @Override
    public void init(GraphicsBackend manager) {
        this.manager = manager;
    }

    @Override
    public void render(float tpf) {
        for (Viewport viewport : viewports) {
            renderViewport(viewport, tpf);
        }
    }

    private void renderViewport(Viewport viewport, float tpf) {
        ShaderProgram shader = this.shader.get();
        shader.use();
        setupViewPort(viewport);

        var scene = viewport.getScene();
        if (scene == null) return;

        scene.doUpdate(tpf);
//        scene.getLightManager().bind(viewport.getCamera(), shader);
//        material.bind(shader, "material");

        shader.setUniform("u_ProjMatrix", viewport.getProjectionMatrix());
        shader.setUniform("u_ViewMatrix", viewport.getViewMatrix());

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glCullFace(GL11.GL_BACK);

        RenderQueue renderQueue = scene.getRenderQueue();
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderQueue.render(manager, StandardRenderTypes.OPAQUE, new RenderTypeHandler() {
            @Override
            public void render(GraphicsBackend manager, GeometryList geometries) {
                geometries.forEach(geometry -> {
                    shader.setUniform("u_ModelMatrix", geometry.getWorldTransform().toTransformMatrix());
                    geometry.getDrawable().draw();
                });
            }
        });

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        Window window = viewport.getWindow();
        if (window != null) {
            window.swapBuffers();
        }
    }

    private void setupViewPort(Viewport viewport) {
        int width = viewport.getWidth();
        int height = viewport.getHeight();
        FrameBuffer frameBuffer = viewport.getFrameBuffer();
        if (width != frameBuffer.getWidth() || height != frameBuffer.getHeight()) {
            frameBuffer.resize(width, height);
        }
        frameBuffer.bind();

        GL11.glViewport(0, 0, width, height);
        Color clearColor = viewport.getClearColor();
        GL11.glClearColor(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), clearColor.getAlpha());
        int clearMask = 0;
        if (viewport.isClearColor()) {
            clearMask |= GL11.GL_COLOR_BUFFER_BIT;
        }
        if (viewport.isClearDepth()) {
            clearMask |= GL11.GL_DEPTH_BUFFER_BIT;
        }
        if (viewport.isClearStencil()) {
            clearMask |= GL11.GL_STENCIL_BUFFER_BIT;
        }
        GL11.glClear(clearMask);
    }

    @Override
    public void dispose() {
    }
}
