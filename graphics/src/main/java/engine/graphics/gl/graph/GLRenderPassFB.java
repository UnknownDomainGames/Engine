package engine.graphics.gl.graph;

import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.gl.util.GLCleaner;
import engine.graphics.graph.ColorOutputInfo;
import engine.graphics.graph.DepthOutputInfo;
import engine.graphics.graph.RenderBufferSize;
import engine.graphics.graph.RenderPassInfo;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.util.BlendMode;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

public final class GLRenderPassFB implements FrameBuffer {

    private static final String BACK_BUFFER = "back";

    private int id;
    private Cleaner.Disposable disposable;

    private ColorOutput[] colorOutputs;
    private DepthOutput depthOutput;

    private boolean enableBlend = false;

    private int width;
    private int height;

    public GLRenderPassFB(RenderPassInfo info, GLRenderTask task) {
        this.id = GL30.glGenFramebuffers();
        this.disposable = GLCleaner.registerFrameBuffer(this, id);

        var colorOutputs = info.getColorOutputs();
        this.colorOutputs = new ColorOutput[colorOutputs.size()];
        for (int i = 0; i < colorOutputs.size(); i++) {
            var colorOutputInfo = colorOutputs.get(i);
            var renderBuffer = task.getRenderBuffer(colorOutputInfo.getColorBuffer());
            this.colorOutputs[i] = new ColorOutput(colorOutputInfo, renderBuffer);
            enableBlend |= colorOutputInfo.getBlendMode() != BlendMode.DISABLED;
        }

        var depthOutputInfo = info.getDepthOutput();
        if (depthOutputInfo != null) {
            var renderBuffer = task.getRenderBuffer(depthOutputInfo.getDepthBuffer());
            this.depthOutput = new DepthOutput(depthOutputInfo, renderBuffer);
        }
    }

    public ColorOutput[] getColorOutputs() {
        return colorOutputs;
    }

    public ColorOutput getColorOutput(int index) {
        return colorOutputs[index];
    }

    public DepthOutput getDepthOutput() {
        return depthOutput;
    }

    public boolean isEnableBlend() {
        return enableBlend;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void resize(int width, int height) {
        throw new UnsupportedOperationException();
    }

    public void beginRenderPass() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);

        RenderBufferSize size = null;
        if (colorOutputs.length > 0) {
            size = colorOutputs[0].getRenderBuffer().getInfo().getSize();
        } else if (depthOutput != null) {
            size = depthOutput.getRenderBuffer().getInfo().getSize();
        }
        width = size.getWidth();
        height = size.getHeight();

        for (int i = 0; i < colorOutputs.length; i++) {
            ColorOutput colorOutput = colorOutputs[i];
            ColorOutputInfo info = colorOutput.info;
            GLRenderTaskRB renderBuffer = colorOutput.renderBuffer;
            if (renderBuffer.isResized()) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i,
                        GL11.GL_TEXTURE_2D, renderBuffer.getTexture().getId(), 0);
            }
            if (info.isClear()) {
                GL30.glClearBufferfv(GL11.GL_COLOR, i, info.getClearColor().toRGBAFloatArray());
            }
            setupBlendMode(i, colorOutput.info.getBlendMode());
        }
        if (depthOutput != null) {
            DepthOutputInfo info = depthOutput.info;
            GLRenderTaskRB renderBuffer = depthOutput.renderBuffer;
            if (renderBuffer.isResized()) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                        GL11.GL_TEXTURE_2D, renderBuffer.getTexture().getId(), 0);
            }
            if (info.isClear()) {
                GL30.glClearBufferfv(GL11.GL_DEPTH, 0, new float[]{info.getClearValue()});
            }
        }
    }

    private void setupBlendMode(int buffer, BlendMode blendMode) {
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

    @Override
    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }

    @Override
    public void bindReadOnly() {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
    }

    @Override
    public void bindDrawOnly() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, id);
    }

    @Override
    public void copyFrom(FrameBuffer source, boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        copyFrom(source, new Vector4i(0, 0, source.getWidth(), source.getHeight()),
                new Vector4i(0, 0, width, height), copyColor, copyDepth, copyStencil, filterMode);
    }

    @Override
    public void copyFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect, boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        GLFrameBuffer.copy(source, sourceRect, this, destRect, copyColor, copyDepth, copyStencil, filterMode);
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        disposable.dispose();
        id = 0;
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    public static final class ColorOutput {
        private final ColorOutputInfo info;
        private final GLRenderTaskRB renderBuffer;

        public ColorOutput(ColorOutputInfo info, GLRenderTaskRB renderBuffer) {
            this.info = info;
            this.renderBuffer = renderBuffer;
        }

        public ColorOutputInfo getInfo() {
            return info;
        }

        public GLRenderTaskRB getRenderBuffer() {
            return renderBuffer;
        }
    }

    public static final class DepthOutput {
        private final DepthOutputInfo info;
        private final GLRenderTaskRB renderBuffer;

        public DepthOutput(DepthOutputInfo info, GLRenderTaskRB renderBuffer) {
            this.info = info;
            this.renderBuffer = renderBuffer;
        }

        public DepthOutputInfo getInfo() {
            return info;
        }

        public GLRenderTaskRB getRenderBuffer() {
            return renderBuffer;
        }
    }
}
