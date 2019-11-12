package nullengine.client.rendering.gl.texture;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLFrameBuffer {

    private final Map<Integer, GLTexture2D.Builder> attachments;

    private int id;
    private int width;
    private int height;

    private final Map<Integer, GLTexture2D> attachedTextures;

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

    private GLFrameBuffer(Map<Integer, GLTexture2D.Builder> attachments, int width, int height) {
        this.attachments = notNull(attachments);
        this.attachedTextures = new HashMap<>();
        this.id = glGenFramebuffers();
        resize(width, height);
    }

    public int getId() {
        return id;
    }

    public GLTexture2D getTexture(int attachment) {
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
            GLTexture2D texture = value.build(width, height);
            attachedTextures.put(key, texture);
            glFramebufferTexture2D(GL_FRAMEBUFFER, key, GL_TEXTURE_2D, texture.getId(), texture.getLevel());
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
        private Map<Integer, GLTexture2D.Builder> attachments = new HashMap<>();
        private int width;
        private int height;

        private Builder() {
        }

        public Builder attachments(int attachment, GLTexture2D.Builder builder) {
            this.attachments.put(attachment, builder);
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
}
