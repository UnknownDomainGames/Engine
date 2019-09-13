package nullengine.client.rendering.util;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBufferShadow extends FrameBuffer {
    public static final int SHADOW_WIDTH = 2048;
    public static final int SHADOW_HEIGHT = 2048;

    @Override
    public void createFrameBuffer() {
        super.createFrameBuffer();
        resize(0, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (dstexPointer == -1) {
            dstexPointer = GL11.glGenTextures();
        }
        GL11.glBindTexture(GL_TEXTURE_2D, dstexPointer);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL11.GL_DEPTH_COMPONENT, GLDataType.FLOAT.glId, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
//        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_REPEAT);
//        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{1f,1f,1f,1f});
        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, dstexPointer, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        unbind();
    }
}
