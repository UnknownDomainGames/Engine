package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.texture.RenderBuffer;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.HashMap;
import java.util.Map;

import static engine.graphics.gl.texture.GLTexture.toGLFilterMode;
import static engine.graphics.gl.util.GLHelper.getMask;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class GLFrameBuffer implements FrameBuffer {

    private static final GLFrameBuffer DEFAULT_FRAME_BUFFER = new GLFrameBuffer();

    private static final int[] glAttachments = {
            GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4,
            GL_COLOR_ATTACHMENT5, GL_COLOR_ATTACHMENT6, GL_COLOR_ATTACHMENT7, GL_COLOR_ATTACHMENT8, GL_COLOR_ATTACHMENT9,
            GL_COLOR_ATTACHMENT10, GL_COLOR_ATTACHMENT11, GL_COLOR_ATTACHMENT12, GL_COLOR_ATTACHMENT13, GL_COLOR_ATTACHMENT14,
            GL_COLOR_ATTACHMENT15, GL_COLOR_ATTACHMENT16, GL_COLOR_ATTACHMENT17, GL_COLOR_ATTACHMENT18, GL_COLOR_ATTACHMENT19,
            GL_COLOR_ATTACHMENT20, GL_COLOR_ATTACHMENT21, GL_COLOR_ATTACHMENT22, GL_COLOR_ATTACHMENT23, GL_COLOR_ATTACHMENT24,
            GL_COLOR_ATTACHMENT25, GL_COLOR_ATTACHMENT26, GL_COLOR_ATTACHMENT27, GL_COLOR_ATTACHMENT28, GL_COLOR_ATTACHMENT29,
            GL_COLOR_ATTACHMENT30, GL_COLOR_ATTACHMENT31, GL_DEPTH_ATTACHMENT, GL_STENCIL_ATTACHMENT, GL_DEPTH_STENCIL_ATTACHMENT
    };

    private final Map<Attachment, AttachableFactory> attachmentFactories;
    private final Map<Attachment, Attachable> attachments;

    private int id;
    private Cleaner.Disposable disposable;
    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    public static GLFrameBuffer getDefaultFrameBuffer() {
        return DEFAULT_FRAME_BUFFER;
    }

    private GLFrameBuffer() {
        this.id = 0;
        this.attachmentFactories = Map.of();
        this.attachments = new HashMap<>();
    }

    private GLFrameBuffer(Map<Attachment, AttachableFactory> attachmentFactories, int width, int height) {
        this.attachmentFactories = attachmentFactories;
        this.attachments = new HashMap<>();
        this.id = glGenFramebuffers();
        this.disposable = GLCleaner.registerFrameBuffer(this, id);
        resize(width, height);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Attachable getAttachable(Attachment attachment) {
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
        bind();
        disposeAttachments();
        attachmentFactories.forEach((attachment, attachableFactory) -> {
            Attachable attachable = attachableFactory.create(width, height);
            if (attachable instanceof RenderBuffer) {
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, getGLAttachment(attachment), GL_RENDERBUFFER, attachable.getId());
            } else if (attachable instanceof Texture2D) {
                int target = attachable.isMultiSample() ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D;
                glFramebufferTexture2D(GL_FRAMEBUFFER, getGLAttachment(attachment), target, attachable.getId(), 0);
            }
            attachments.put(attachment, attachable);
        });
    }

    private int getGLAttachment(Attachment attachment) {
        return glAttachments[attachment.ordinal()];
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    @Override
    public void bindReadOnly() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
    }

    @Override
    public void bindDrawOnly() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
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

        disposeAttachments();
    }

    private void disposeAttachments() {
        attachments.forEach((key, value) -> value.dispose());
        attachments.clear();
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    public static final class Builder implements FrameBuffer.Builder {
        private Map<Attachment, AttachableFactory> attachableFactories = new HashMap<>();

        private Builder() {
        }

        @Override
        public Builder attach(Attachment attachment, Texture2D.Builder builder) {
            return attach(attachment, builder::build);
        }

        public Builder attach(Attachment attachment, GLTexture2DMultiSample.Builder builder) {
            return attach(attachment, builder::build);
        }

        @Override
        public Builder attach(Attachment attachment, RenderBuffer.Builder builder) {
            return attach(attachment, builder::build);
        }

        @Override
        public Builder attach(Attachment attachment, AttachableFactory factory) {
            this.attachableFactories.put(attachment, factory);
            return this;
        }

        @Override
        public GLFrameBuffer build(int width, int height) {
            return new GLFrameBuffer(Map.copyOf(attachableFactories), width, height);
        }
    }
}
