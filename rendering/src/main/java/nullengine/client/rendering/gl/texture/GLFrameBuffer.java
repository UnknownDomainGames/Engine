package nullengine.client.rendering.gl.texture;

import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL30.*;

public class GLFrameBuffer {

    private final Map<Integer, TextureFactory> attachments;

    private int id;
    private int width;
    private int height;

    private final Map<Integer, GLTexture> attachedTextures;

    public static Builder builder() {
        return new Builder();
    }

    public static GLFrameBuffer createRGB16FDepth24Stencil8FrameBuffer(int width, int height) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_COLOR_ATTACHMENT0, GLTexture2D.builder()
                        .internalFormat(GL_RGB16F).format(GL_RGB).type(GL_FLOAT))
                .attachments(GL_DEPTH_STENCIL_ATTACHMENT, GLTexture2D.builder()
                        .internalFormat(GL_DEPTH24_STENCIL8).format(GL_DEPTH_STENCIL).type(GL_UNSIGNED_INT_24_8))
                .build();
    }

    public static GLFrameBuffer createMultiSampleRGB16FDepth24Stencil8FrameBuffer(int width, int height, int sample) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_COLOR_ATTACHMENT0, GLTexture2DMultiSample.builder().internalFormat(GL_RGB16F).sample(sample))
                .attachments(GL_DEPTH_STENCIL_ATTACHMENT, GLTexture2DMultiSample.builder().internalFormat(GL_DEPTH24_STENCIL8).sample(sample))
                .build();
    }

    public static GLFrameBuffer createDepthFrameBuffer(int width, int height) {
        return builder()
                .width(width)
                .height(height)
                .attachments(GL_DEPTH_ATTACHMENT, GLTexture2D.builder()
                        .internalFormat(GL_DEPTH_COMPONENT).format(GL_DEPTH_COMPONENT).type(GL_FLOAT))
                .build();
    }

    public static void bindScreenFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    private GLFrameBuffer(Map<Integer, TextureFactory> attachments, int width, int height) {
        this.attachments = notNull(attachments);
        this.attachedTextures = new HashMap<>();
        this.id = glGenFramebuffers();
        resize(width, height);
    }

    public int getId() {
        return id;
    }

    public GLTexture getTexture(int attachment) {
        return attachedTextures.get(attachment);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resize(int width, int height) {
        if (id == 0) throw new IllegalStateException("Frame buffer has been disposed");
        this.width = width;
        this.height = height;
        disposeAttachedTextures();
        bind();
        attachments.forEach((key, value) -> {
            GLTexture texture = value.create(width, height);
            attachedTextures.put(key, texture);
            glFramebufferTexture2D(GL_FRAMEBUFFER, key, texture.getTarget(), texture.getId(), 0);
        });
    }

    public void bind() {
        bind(GL_FRAMEBUFFER);
    }

    public void bindReadOnly() {
        bind(GL_READ_FRAMEBUFFER);
    }

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

    public void dispose() {
        if (id == 0) return;
        glDeleteFramebuffers(id);
        id = 0;

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

        public Builder attachments(int attachment, GLTexture2D.Builder builder) {
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
        GLTexture create(int width, int height);
    }
}
