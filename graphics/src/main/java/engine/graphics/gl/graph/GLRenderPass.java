package engine.graphics.gl.graph;

import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.graph.*;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.util.CullMode;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.stream.Collectors;

public final class GLRenderPass implements RenderPass {

    private final RenderPassInfo info;
    private final GLRenderTask task;

    private final GLRenderPassFB frameBuffer;
    private final List<GLDrawer> drawers;

    public GLRenderPass(RenderPassInfo info, GLRenderTask task) {
        this.info = info;
        this.task = task;
        this.frameBuffer = new GLRenderPassFB(info, task);
        this.drawers = info.getDrawers().stream()
                .map(drawerInfo -> new GLDrawer(drawerInfo, this))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public RenderPassInfo getInfo() {
        return info;
    }

    @Override
    public RenderTask getRenderTask() {
        return task;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public void draw(Frame frame) {
        frameBuffer.beginRenderPass();
        setupViewport();
        setupCullMode(info.getCullMode());
        setupDepthTest(info.getDepthOutput());
        setupBlend();
        drawers.forEach(drawer -> drawer.draw(frame));
    }

    private void setupViewport() {
        GLRenderPassFB.ColorOutput colorOutput = frameBuffer.getColorOutput(0);
        GLTexture2D texture = colorOutput.getRenderBuffer().getTexture();
        GL11.glViewport(0, 0, texture.getWidth(), texture.getHeight());
    }

    private void setupCullMode(CullMode cullMode) {
        switch (cullMode) {
            case DISABLED:
                GL11.glDisable(GL11.GL_CULL_FACE);
                break;
            case CULL_FRONT:
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_FRONT);
                break;
            case CULL_BACK:
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_BACK);
                break;
            case CULL_ALL:
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_FRONT_AND_BACK);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void setupDepthTest(DepthOutputInfo depthOutput) {
        if (depthOutput != null && depthOutput.isEnable()) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(depthOutput.isWritable());
            GL11.glDepthFunc(GLHelper.toGLCompareFunc(depthOutput.getCompareMode()));
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    private void setupBlend() {
        if (frameBuffer.isEnableBlend()) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void dispose() {
        frameBuffer.dispose();
        drawers.forEach(GLDrawer::dispose);
    }
}
