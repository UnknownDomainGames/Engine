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
    protected int fboId = -1;
    protected int texId = -1;
    protected int dstexid = -1;
    protected int width;
    protected int height;

    public void createFrameBuffer() {
        if (fboId == -1) {
            fboId = GL30.glGenFramebuffers();
        }
    }

    public void resize(int width, int height) {
        if (texId == -1) {
            texId = GL11.glGenTextures();
        }
        this.width = width;
        this.height = height;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGB, GL11.GL_FLOAT, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        bind();
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
        if (dstexid == -1) {
            dstexid = GL11.glGenTextures();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dstexid);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH24_STENCIL8, width, height, 0, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, dstexid, 0);
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
        GL30.glBindFramebuffer(target, getFboId());
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void unbindAll() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
    }

    public void deleteFrameBuffer() {
        if (fboId != -1) {
            GL30.glDeleteFramebuffers(fboId);
            fboId = -1;
        }
    }

    public int getTexId() {
        return texId;
    }

    public int getDstexid() {
        return dstexid;
    }

    public int getFboId() {
        return fboId;
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
            Platform.getLogger().warn(String.format("frame buffer #%d incomplete!", getFboId()));
        }
        unbind();
    }

}
