package nullengine.client.rendering.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBufferMultiSampled extends FrameBuffer {
    @Override
    public void resize(int width, int height) {
        if (texId == -1) {
            texId = GL11.glGenTextures();
        }
        this.width = width;
        this.height = height;
        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, texId);

        GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL30.GL_RGB16F, width, height, true);
        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);

        bind();
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, texId, 0);
        if (dstexid == -1) {
            dstexid = GL11.glGenTextures();
        }

        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, dstexid);

        GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL30.GL_DEPTH24_STENCIL8, width, height, true);
        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL32.GL_TEXTURE_2D_MULTISAMPLE, dstexid, 0);
        unbind();
    }
}
