package engine.graphics.gl.graph;

import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.graph.*;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.util.BlendMode;
import engine.graphics.util.CullMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import java.util.List;
import java.util.stream.Collectors;

public final class GLRenderPass implements RenderPass {

    private final RenderPassInfo info;
    private final GLRenderTask task;

    private final GLFrameBuffer frameBuffer;
    private final List<GLDrawer> drawers;

    private ColorOutput[] colorOutputs;
    private DepthOutput depthOutput;

    private boolean enableBlend = false;

    public GLRenderPass(RenderPassInfo info, GLRenderTask task) {
        this.info = info;
        this.task = task;
        this.frameBuffer = isBackBuffer(info) ? GLFrameBuffer.getBackBuffer() : createFrameBuffer(info, task);
        this.drawers = info.getDrawers().stream()
                .map(drawerInfo -> new GLDrawer(drawerInfo, this))
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean isBackBuffer(RenderPassInfo info) {
        List<ColorOutputInfo> colorOutputs = info.getColorOutputs();
        if (colorOutputs.size() != 1) return false;
        if (colorOutputs.get(0).getColorBuffer() != null) return false;

        DepthOutputInfo depthOutput = info.getDepthOutput();
        if (depthOutput != null && depthOutput.getDepthBuffer() != null) return false;
        return true;
    }

    private GLFrameBuffer createFrameBuffer(RenderPassInfo info, GLRenderTask task) {
        var colorOutputs = info.getColorOutputs();
        this.colorOutputs = new ColorOutput[colorOutputs.size()];
        for (int i = 0; i < colorOutputs.size(); i++) {
            var colorOutputInfo = colorOutputs.get(i);
            var renderBuffer = task.getRenderBufferProxy(colorOutputInfo.getColorBuffer());
            this.colorOutputs[i] = new ColorOutput(colorOutputInfo, renderBuffer);
            enableBlend |= colorOutputInfo.getBlendMode() != BlendMode.DISABLED;
        }

        var depthOutputInfo = info.getDepthOutput();
        if (depthOutputInfo != null) {
            var renderBuffer = task.getRenderBufferProxy(depthOutputInfo.getDepthBuffer());
            this.depthOutput = new DepthOutput(depthOutputInfo, renderBuffer);
        }

        return new GLFrameBuffer();
    }

    @Override
    public RenderPassInfo getInfo() {
        return info;
    }

    public String getName() {
        return info.getName();
    }

    public List<String> getDependencies() {
        return info.getDependencies();
    }

    @Override
    public GLRenderTask getRenderTask() {
        return task;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public void draw(FrameContext frameContext) {
        setupFrameBuffer(frameContext.getFrame().isResized());
        setupViewport();
        setupCullMode(info.getCullMode());
        setupDepthTest(info.getDepthOutput());
        setupBlend();
        drawers.forEach(drawer -> drawer.draw(frameContext));
    }

    private void setupFrameBuffer(boolean resized) {
        frameBuffer.bind();

        if (resized) frameBuffer.reset();
        int[] drawbuffers = new int[colorOutputs.length];
        for (int i = 0; i < colorOutputs.length; i++) {
            ColorOutput colorOutput = colorOutputs[i];
            ColorOutputInfo info = colorOutput.info;
            GLRenderBufferProxy renderBuffer = colorOutput.renderBuffer;
            if (resized) frameBuffer.attach(GL30.GL_COLOR_ATTACHMENT0 + i, renderBuffer.getTexture());
            drawbuffers[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
            if (info.isClear()) GL30.glClearBufferfv(GL11.GL_COLOR, i, info.getClearColor().toRGBAFloatArray());
            setupBufferBlend(i, colorOutput.info.getBlendMode());
        }
        GL30.glDrawBuffers(drawbuffers);

        if (depthOutput != null) {
            DepthOutputInfo info = depthOutput.info;
            GLRenderBufferProxy renderBuffer = depthOutput.renderBuffer;
            if (resized) frameBuffer.attach(GL30.GL_DEPTH_ATTACHMENT, renderBuffer.getTexture());
            if (info.isClear()) GL30.glClearBufferfv(GL11.GL_DEPTH, 0, new float[]{info.getClearValue()});
        }
    }

    private void setupBufferBlend(int buffer, BlendMode blendMode) {
        if (!enableBlend) return;
        switch (blendMode) {
            case DISABLED:
                GL40.glBlendFunci(buffer, GL11.GL_ONE, GL11.GL_ZERO);
                break;
            case MIX:
                GL40.glBlendFunci(buffer, GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                break;
        }
    }

    private void setupViewport() {
        GL11.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
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
        if (enableBlend) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void dispose() {
        frameBuffer.dispose();
        drawers.forEach(GLDrawer::dispose);
    }

    public static final class ColorOutput {
        private final ColorOutputInfo info;
        private final GLRenderBufferProxy renderBuffer;

        public ColorOutput(ColorOutputInfo info, GLRenderBufferProxy renderBuffer) {
            this.info = info;
            this.renderBuffer = renderBuffer;
        }

        public ColorOutputInfo getInfo() {
            return info;
        }

        public GLRenderBufferProxy getRenderBuffer() {
            return renderBuffer;
        }
    }

    public static final class DepthOutput {
        private final DepthOutputInfo info;
        private final GLRenderBufferProxy renderBuffer;

        public DepthOutput(DepthOutputInfo info, GLRenderBufferProxy renderBuffer) {
            this.info = info;
            this.renderBuffer = renderBuffer;
        }

        public DepthOutputInfo getInfo() {
            return info;
        }

        public GLRenderBufferProxy getRenderBuffer() {
            return renderBuffer;
        }
    }
}
