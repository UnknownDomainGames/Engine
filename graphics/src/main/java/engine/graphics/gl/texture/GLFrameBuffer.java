package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.HashMap;
import java.util.Map;

import static engine.graphics.gl.texture.GLTexture.toGLFilterMode;
import static engine.graphics.gl.util.GLHelper.getMask;
import static org.lwjgl.opengl.GL30.*;

public final class GLFrameBuffer implements FrameBuffer {

    private static final GLFrameBuffer DEFAULT_FRAME_BUFFER = new GLFrameBuffer();

    private final Map<Integer, AttachableFactory> attachmentFactories;
    private final Map<Integer, Attachable> attachments;

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

    private GLFrameBuffer(Map<Integer, AttachableFactory> attachmentFactories, int width, int height) {
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

    public Attachable getAttachable(int attachment) {
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
        if (this.width == width && this.height == height) return;
        this.width = width;
        this.height = height;

        if (id == 0) return;

        bind();
        attachmentFactories.forEach((attachment, attachableFactory) -> {
            Attachable attachable = attachableFactory.create(width, height);
            if (attachable.getTarget() == GL_RENDERBUFFER) {
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, attachable.getId());
            } else {
                glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, attachable.getTarget(), attachable.getId(), 0);
            }
            Attachable oldAttachable = attachments.put(attachment, attachable);
            if (oldAttachable != null) oldAttachable.dispose();
        });
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
        copy(source, sourceRect, this, destRect, copyColor, copyDepth, copyStencil, filterMode);
    }

    public static void copy(FrameBuffer src, Vector4ic srcRect, FrameBuffer dest, Vector4ic destRect,
                            boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        copy(src, srcRect, dest, destRect, getMask(copyColor, copyDepth, copyStencil), toGLFilterMode(filterMode));
    }

    public static void copy(FrameBuffer src, Vector4ic srcRect, FrameBuffer dest, Vector4ic destRect, int mask, int filter) {
        src.bindReadOnly();
        dest.bindDrawOnly();
        glBlitFramebuffer(srcRect.x(), srcRect.y(), srcRect.z(), srcRect.w(),
                destRect.x(), destRect.y(), destRect.z(), destRect.w(), mask, filter);
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        disposable.dispose();
        id = 0;

        attachments.values().forEach(Attachable::dispose);
        attachments.clear();
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    @FunctionalInterface
    public interface AttachableFactory {
        Attachable create(int width, int height);
    }

    public interface Attachable {
        TextureFormat getFormat();

        int getId();

        int getTarget();

        boolean isMultiSample();

        int getWidth();

        int getHeight();

        void dispose();

        boolean isDisposed();
    }

    public static final class Builder {
        private Map<Integer, AttachableFactory> attachableFactories = new HashMap<>();

        private Builder() {
        }

        public Builder attach(int attachment, GLTexture2D.Builder builder) {
            return attach(attachment, builder::build);
        }

        public Builder attach(int attachment, GLTexture2DMultiSample.Builder builder) {
            return attach(attachment, builder::build);
        }

        public Builder attach(int attachment, AttachableFactory factory) {
            this.attachableFactories.put(attachment, factory);
            return this;
        }

        public GLFrameBuffer build(int width, int height) {
            return new GLFrameBuffer(Map.copyOf(attachableFactories), width, height);
        }
    }
}
