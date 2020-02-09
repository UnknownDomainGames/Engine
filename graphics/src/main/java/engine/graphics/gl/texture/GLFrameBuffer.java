package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.opengl.GL30.*;

public class GLFrameBuffer implements FrameBuffer {

    private static final GLFrameBuffer DEFAULT_FRAME_BUFFER = new GLFrameBuffer();

    private int id;
    private Cleaner.Disposable disposable;
    private int width;
    private int height;

    private final Map<Integer, TextureFactory> attachments;

    private final Map<Integer, Texture2D> attachedTextures;

    public static Builder builder() {
        return new Builder();
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
        this.attachments = Map.of();
        this.attachedTextures = new HashMap<>();
    }

    private GLFrameBuffer(Map<Integer, TextureFactory> attachments, int width, int height) {
        this.attachments = notNull(attachments);
        this.attachedTextures = new HashMap<>();
        this.id = glGenFramebuffers();
        this.disposable = GLCleaner.registerFrameBuffer(this, id);
        resize(width, height);
    }

    @Override
    public int getId() {
        return id;
    }

    public Texture2D getTexture(int attachment) {
        return attachedTextures.get(attachment);
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
        if (id == -1) throw new IllegalStateException("Frame buffer has been disposed");
        this.width = width;
        this.height = height;
        disposeAttachedTextures();
        bind();
        attachments.forEach((key, value) -> {
            Texture2D texture = value.create(width, height);
            GLTexture glTexture = (GLTexture) texture;
            attachedTextures.put(key, texture);
            glFramebufferTexture2D(GL_FRAMEBUFFER, key, glTexture.getTarget(), glTexture.getId(), 0);
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

    public void blitFrom(GLFrameBuffer source) {
        blitFrom(source, new Vector4i(0, 0, source.width, source.height), new Vector4i(0, 0, width, height));
    }

    public void blitFrom(GLFrameBuffer source, Vector4ic sourceRect, Vector4ic destRect) {
        source.bindReadOnly();
        bindDrawOnly();
        glBlitFramebuffer(sourceRect.x(), sourceRect.y(), sourceRect.z(), sourceRect.w(), destRect.x(), destRect.y(), destRect.z(), destRect.w(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }

    @Override
    public void dispose() {
        if (id == -1) return;
        disposable.dispose();
        id = -1;

        disposeAttachedTextures();
    }

    private void disposeAttachedTextures() {
        attachedTextures.forEach((key, value) -> value.dispose());
        attachedTextures.clear();
    }

    public static final class Builder {
        private Map<Integer, TextureFactory> attachments = new HashMap<>();
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

        public Builder attachments(int attachment, TextureFactory factory) {
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
    public interface TextureFactory {
        Texture2D create(int width, int height);
    }
}
