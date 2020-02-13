package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.*;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.HashMap;
import java.util.Map;

import static engine.graphics.gl.texture.GLTexture.toGLFilterMode;
import static engine.graphics.gl.util.GLHelper.getMask;
import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class GLFrameBuffer implements FrameBuffer {

    private static final GLFrameBuffer DEFAULT_FRAME_BUFFER = new GLFrameBuffer();

    private int id;
    private Cleaner.Disposable disposable;
    private int width;
    private int height;

    private final Map<Integer, AttachableFactory> attachmentFactories;

    private final Map<Integer, Attachable> attachments;

    public static Builder builder() {
        return new Builder();
    }

    public static GLFrameBuffer createRGB16FFrameBuffer(int width, int height) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_COLOR_ATTACHMENT0, Texture2D.builder().format(TextureFormat.RGB16F))
                .build();
    }

    public static GLFrameBuffer createRGB16FDepth24Stencil8FrameBuffer(int width, int height) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_COLOR_ATTACHMENT0, Texture2D.builder().format(TextureFormat.RGB16F))
                .attachments(GL_DEPTH_STENCIL_ATTACHMENT, Texture2D.builder().format(TextureFormat.DEPTH24_STENCIL8))
                .build();
    }

    public static GLFrameBuffer createMultiSampleRGB16FDepth24Stencil8FrameBuffer(int width, int height, Sampler sampler) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_COLOR_ATTACHMENT0, GLTexture2DMultiSample.builder().format(TextureFormat.RGB16F).sampler(sampler))
                .attachments(GL_DEPTH_STENCIL_ATTACHMENT, GLTexture2DMultiSample.builder().format(TextureFormat.DEPTH24_STENCIL8).sampler(sampler))
                .build();
    }

    public static GLFrameBuffer createDepthFrameBuffer(int width, int height) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_DEPTH_ATTACHMENT, Texture2D.builder().format(TextureFormat.DEPTH32))
                .build();
    }

    public static GLFrameBuffer getDefaultFrameBuffer() {
        return DEFAULT_FRAME_BUFFER;
    }

    public static void bindDefaultFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    private GLFrameBuffer() {
        this.id = 0;
        this.attachmentFactories = Map.of();
        this.attachments = new HashMap<>();
    }

    private GLFrameBuffer(Map<Integer, AttachableFactory> attachmentFactories, int width, int height) {
        this.attachmentFactories = notNull(attachmentFactories);
        this.attachments = new HashMap<>();
        this.id = glGenFramebuffers();
        this.disposable = GLCleaner.registerFrameBuffer(this, id);
        resize(width, height);
    }

    @Override
    public int getId() {
        return id;
    }

    public Attachable getTexture(int attachment) {
        return attachments.get(attachment);
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
        this.width = width;
        this.height = height;
        if (id == 0) return;
        disposeAttachedTextures();
        bind();
        attachmentFactories.forEach((attachment, attachableFactory) -> {
            Attachable attachable = attachableFactory.create(width, height);
            if (attachable instanceof RenderBuffer) {
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, attachable.getId());
            } else if (attachable instanceof Texture2D) {
                int target = attachable.isMultiSample() ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D;
                glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, target, attachable.getId(), 0);
            }
        });
    }

    @Override
    public void bind() {
        bind(GL_FRAMEBUFFER);
    }

    @Override
    public void bindReadOnly() {
        bind(GL_READ_FRAMEBUFFER);
    }

    @Override
    public void bindDrawOnly() {
        bind(GL_DRAW_FRAMEBUFFER);
    }

    protected void bind(int target) {
        glBindFramebuffer(target, id);
    }

    @Override
    public void copyFrom(FrameBuffer source, boolean copyColor, boolean copyDepth, boolean copyStencil,
                         FilterMode filterMode) {
        copyFrom(source, new Vector4i(0, 0, source.getWidth(), source.getHeight()),
                new Vector4i(0, 0, width, height), copyColor, copyDepth, copyStencil, filterMode);
    }

    @Override
    public void copyFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect,
                         boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        copyFrom(source, sourceRect, destRect, getMask(copyColor, copyDepth, copyStencil), toGLFilterMode(filterMode));
    }

    public void copyFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect, int mask, int filter) {
        source.bindReadOnly();
        bindDrawOnly();
        glBlitFramebuffer(sourceRect.x(), sourceRect.y(), sourceRect.z(), sourceRect.w(),
                destRect.x(), destRect.y(), destRect.z(), destRect.w(), mask, filter);
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        disposable.dispose();
        id = 0;

        disposeAttachedTextures();
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    private void disposeAttachedTextures() {
        attachments.forEach((key, value) -> value.dispose());
        attachments.clear();
    }

    public static final class Builder {
        private Map<Integer, AttachableFactory> attachments = new HashMap<>();
        private int width;
        private int height;

        private Builder() {
        }

        public Builder attachments(int attachment, Texture2D.Builder builder) {
            return attachments(attachment, builder::build);
        }

        public Builder attachments(int attachment, GLTexture2DMultiSample.Builder builder) {
            return attachments(attachment, builder::build);
        }

        public Builder attachments(int attachment, AttachableFactory factory) {
            this.attachments.put(attachment, factory);
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public GLFrameBuffer build() {
            return new GLFrameBuffer(Map.copyOf(attachments), width, height);
        }
    }

    @FunctionalInterface
    public interface AttachableFactory {
        FrameBuffer.Attachable create(int width, int height);
    }
}
