package nullengine.client.rendering.util;

import nullengine.Platform;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    protected int fboPointer = -1;
    protected int texPointer = -1;
    protected int dstexPointer = -1;
    protected int width;
    protected int height;

    public void createFrameBuffer() {
        if (fboPointer == -1) {
            fboPointer = GL30.glGenFramebuffers();
        }
    }

    public void resize(int width, int height) {
        if (texPointer == -1) {
            texPointer = GL11.glGenTextures();
        }
        this.width = width;
        this.height = height;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texPointer);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGB, GL11.GL_FLOAT, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        bind();
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texPointer, 0);
        if (dstexPointer == -1) {
            dstexPointer = GL11.glGenTextures();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dstexPointer);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH24_STENCIL8, width, height, 0, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, dstexPointer, 0);
        unbind();
    }

    public void bind() {
        bind(GL_FRAMEBUFFER);
    }

    public void bindReadOnly() {
        bind(GL30.GL_READ_FRAMEBUFFER);
    }

    public void bindDrawOnly() {
        bind(GL30.GL_DRAW_FRAMEBUFFER);
    }

    protected void bind(int target) {
        GL30.glBindFramebuffer(target, getFboPointer());
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void unbindAll() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
    }

    public int getTexPointer() {
        return texPointer;
    }

    public int getDstexPointer() {
        return dstexPointer;
    }

    public int getFboPointer() {
        return fboPointer;
    }

    public void blitFrom(FrameBuffer source) {
        blitFrom(source, new Vector4i(0, 0, source.width, source.height), new Vector4i(0, 0, width, height));
    }

    public void blitFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect) {
        source.bindReadOnly();
        this.bindDrawOnly();
        glBlitFramebuffer(sourceRect.x(), sourceRect.y(), sourceRect.z(), sourceRect.w(), destRect.x(), destRect.y(), destRect.z(), destRect.w(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }

    public void check() {
        bind();
        if (GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Platform.getLogger().warn(String.format("frame buffer #%d incomplete!", getFboPointer()));
        }
        unbind();
    }

    public void dispose() {
        deleteFrameBuffer();
        deleteTexture();
    }

    private void deleteFrameBuffer() {
        if (fboPointer != -1) {
            GL30.glDeleteFramebuffers(fboPointer);
            fboPointer = -1;
        }
    }

    private void deleteTexture() {
        if (texPointer != -1) {
            GL11.glDeleteTextures(texPointer);
            texPointer = -1;
        }

        if (dstexPointer != -1) {
            GL11.glDeleteTextures(dstexPointer);
            dstexPointer = -1;
        }
    }
}
